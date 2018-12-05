package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.github.misterchangray.service.po.AccountInfo;
import com.github.misterchangray.service.po.Order;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 交易实现得是v1版本得方法
 */
@Service
public class BitfinexService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(BitfinexService.class);
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();
    @Autowired
    ThreadPoolTaskExecutor executor;

    private static final String ALGORITHM_HMACSHA384 = "HmacSHA384";
    private static AtomicLong nonce = new AtomicLong(System.currentTimeMillis());




    public static String makeName(String type) {
        return type.replace("_", "").replace("usdt", "usd");
    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if(null != type) type =  makeName(type);

        DepthVo depthVo = depth.get(type.toUpperCase());
        if(null == depthVo) return null;
        Long tmp = System.currentTimeMillis() - depthVo.getUpdateTime();
        return depthVo;
    }

    public void openSocket() {
        //保存websocket连接
        WebSocket ws = null;
        Map<Integer, String > chanIdMapBuilder = new ConcurrentHashMap<>();  //资方通道代码映射代码
        try {
            ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.Bitfinex.getOrgSocketUrlSpot());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(BitfinexService.class);

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- Bitfinex websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket();
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- Bitfinex websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                    for(String type : Const.types) {

                        //订阅ticker数据
                        websocket.sendText("{\"event\":\"subscribe\",\"channel\":\"ticker\",\"symbol\":\"" + makeName(type) + "\"}");

                        //订阅book数据
                        websocket.sendText("{\"event\":\"subscribe\",\"channel\":\"book\",\"symbol\":\"" + makeName(type) + "\",\"freq\":\"F0\"}");
                    }
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    if(jsonNode.isObject() && null != jsonNode.get("chanId")) {
                        //{"event":"subscribed","channel":"ticker","chanId":598,"symbol":"tETHUSD","pair":"ETHUSD"}
                        //是否订阅成功
                      chanIdMapBuilder.put(jsonNode.get("chanId").asInt(), jsonNode.get("pair").asText() + jsonNode.get("channel").asText());
                    }
                    if(jsonNode.get(1).isTextual() && "hb".equals(jsonNode.get(1).asText())) return;

                    if(jsonNode.isArray() && chanIdMapBuilder.get(jsonNode.get(0).asInt()).contains("ticker")) {
                        //[3251,[6730.3,24.19350033,6730.4,24.46335652,389.8,0.0615,6731.7,32965.79221873,6771,6327.1]]
                        //订阅成功后返回得数据
                        String name = chanIdMapBuilder.get(jsonNode.get(0).asInt());
                        name = name.replace("ticker", "");
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if(null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if(Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        jsonNode = jsonNode.get(1);
                        secPrice.add(new Ticker(jsonNode.get(6).asText(),jsonNode.get(8).asText(),jsonNode.get(8).asText(),
                                jsonNode.get(0).asText(),jsonNode.get(2).asText(),jsonNode.get(7).asText()));
                        tickers.put(name,  secPrice);
                    }


                    if(chanIdMapBuilder.get(jsonNode.get(0).asInt()).contains("book")) {
                        String name = chanIdMapBuilder.get(jsonNode.get(0).asInt());
                        name = name.replace("book", "");
                        DepthVo depthVoMap = depth.get(makeName(name));
                        if(null == depthVoMap) depthVoMap = new DepthVo();

                        List<Depth> asks = depthVoMap.getAsks();
                        List<Depth> bids = depthVoMap.getBids();
                        if(null == asks) asks = ListBuilder.build();
                        if(null == bids) bids = ListBuilder.build();

                        Map<String, Double> tmpMapAsks = new ConcurrentHashMap<>();
                        Map<String, Double> tmpMapBids = new ConcurrentHashMap<>();
                        for(Depth depth : asks) {
                            tmpMapAsks.put(String.valueOf(depth.getPrice()), depth.getQty());
                        }
                        for(Depth depth : bids) {
                            tmpMapBids.put(String.valueOf(depth.getPrice()), depth.getQty());
                        }
                        if(null != jsonNode.get(1) && jsonNode.get(1).isArray()
                                && null != jsonNode.get(1).get(0) && jsonNode.get(1).get(0).isArray()) {
                            for(JsonNode tmp : jsonNode.get(1)) {
                                //[6398.3,24,5.75906204]
                                double price = tmp.get(0).asDouble();
                                int count = tmp.get(1).asInt();
                                double amount = tmp.get(2).asDouble();
                                if(tmp.isArray()) {
                                    if(count > 0) {
                                        if(amount > 0) {
                                            tmpMapBids.put(String.valueOf(price), amount);
                                        }
                                        if(amount < 0){
                                            tmpMapAsks.put(String.valueOf(price), Math.abs(amount));
                                        }
                                    }
                                }
                            }
                        } else if(null != jsonNode.get(1) && jsonNode.get(1).isArray()
                                && null != jsonNode.get(1).get(0) && false == jsonNode.get(1).get(0).isArray()) {
                            JsonNode tmp = jsonNode.get(1);

                            double price = tmp.get(0).asDouble();
                            int count = tmp.get(1).asInt();
                            double amount = tmp.get(2).asDouble();
                            if(count > 0) {
                                if(amount > 0) {
                                    tmpMapBids.put(String.valueOf(price), amount);
                                }
                                if(amount < 0){
                                    tmpMapAsks.put(String.valueOf(price), Math.abs(amount));
                                }
                            } else if(count == 0) {
                                if(1 == amount) {
                                    tmpMapBids.remove(String.valueOf(price));
                                }
                                if(-1 == amount) {
                                    tmpMapAsks.remove(String.valueOf(price));
                                }
                            }
                        }

                        asks = ListBuilder.build();
                        bids = ListBuilder.build();
                        for(String k :tmpMapAsks.keySet()) {
                            asks.add(new Depth(Double.valueOf(k), tmpMapAsks.get(k)));
                        }
                        for(String k :tmpMapBids.keySet()) {
                            bids.add(new Depth(Double.valueOf(k), tmpMapBids.get(k)));
                        }
                        depthVoMap.setAsks(asks);
                        depthVoMap.setBids(bids);
                        depthVoMap.setUpdateTime(System.currentTimeMillis());
                        depth.put(makeName(name), depthVoMap);
//                        if(name.equals("ETHUSD")) {
//                            logger.info("买1:{},{},买2:{},{};卖1：{}，{}；卖2：{}，{}",
//                                    depthVoMap.getBids().get(0).getPrice(),depthVoMap.getBids().get(0).getQty(),
//                                    depthVoMap.getBids().get(1).getPrice(),depthVoMap.getBids().get(1).getQty(),
//                                    depthVoMap.getAsks().get(0).getPrice(),depthVoMap.getAsks().get(0).getQty(),
//                                    depthVoMap.getAsks().get(1).getPrice(),depthVoMap.getAsks().get(1).getQty());
//                        }
                    }
                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- Bitfinex websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocket();
        }
    }

    @Override
    public Boolean init() {
        openSocket();
        return super.init();
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if(QuartzEnum.Hour == quartzEnum) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    spiderAccountData(execTime);
                }
            });
        }
        return null;
    }


    @Override
    public synchronized ResultSet<String> buy(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey ) {
        String api = "/v1/order/new";
        MapBuilder postData = MapBuilder.build()
                .add("request", api)
                .add("nonce", String.valueOf((nonce.incrementAndGet())))
                .add("symbol", makeName(type.toString()))
                .add("side", Order.direc_buy)
                .add("amount", String.valueOf(amount))
                .add("ocoorder", false);
        if(null == price) {
            postData.add("type", "exchange market");
        } else {
            postData .add("type", "exchange limit");
            postData .add("price", String.valueOf(price));
        }
        String plyload = JSONUtils.obj2json(postData);
        // API v1
        try {
            String  res = HttpUtilManager.getInstance().requestHttpPost(
                    OrgsInfoEnum.Bitfinex.getOrgRestUrlSpot() + api,null, postData, createAuthHttpHeaders(plyload, null, aKey, sKey));
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result && null != result.get("id") && result.get("id").asText().length() > 3) {
                return ResultSet.build(ResultEnum.SUCCESS).setData( result.get("id").asText());
            } else {
                //{"message":"Invalid order: not enough tradable balance for 5.0 BTCUSD at 6200.0"}
                if(res.contains("not enough tradable balance")) {
                    //处理余额不足
                    return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
                }
//                logger.warn("bitfinex buy 下单失败:{}", res);
                return ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
//            logger.warn("bitfinex buy 下单失败:{}", e.toString());
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public synchronized ResultSet<String> sell(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        String api = "/v1/order/new";
        MapBuilder postData = MapBuilder.build()
                .add("request", api)
                .add("nonce", String.valueOf((nonce.incrementAndGet())))
                .add("symbol", makeName(type.toString()))
                .add("amount", String.valueOf(amount))
                .add("side", Order.direc_sell)
                .add("ocoorder", false);
        if(null == price) {
            postData.add("type", "exchange market");
        } else {
            postData.add("type", "exchange limit");
            postData.add("price", String.valueOf(price));
        }
        String plyload = JSONUtils.obj2json(postData);
        // API v1
        try {
            String  res = HttpUtilManager.getInstance().requestHttpPost(
                    OrgsInfoEnum.Bitfinex.getOrgRestUrlSpot() + api, null,postData, createAuthHttpHeaders(plyload, null, aKey, sKey));
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result && null != result.get("id") && result.get("id").asText().length() > 3) {
                return ResultSet.build(ResultEnum.SUCCESS).setData(result.get("id").asText());
            } else {
                //{"message":"Invalid order: not enough tradable balance for 5.0 BTCUSD at 6200.0"}
                if(res.contains("not enough tradable balance")) {
                    //处理余额不足
                    return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
                }
                logger.error("bitfinex sell 下单失败:{}", res);
                return  ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
            logger.error("bitfinex sell 下单失败:{}", e.toString());
            return  ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public synchronized ResultSet<String> cancelOrder(String orderId, CoinSpot symbol, String aKey, String sKey) {
        String api = "/v1/order/cancel";
        if(null == orderId) return null;
        MapBuilder postData = MapBuilder.build()
                .add("request", api)
                .add("nonce", String.valueOf((nonce.incrementAndGet())))
                .add("order_id", Long.parseLong(orderId));
        String plyload = JSONUtils.obj2json(postData);
        // API v1
        try {
            String  res = HttpUtilManager.getInstance().requestHttpPost(
                    OrgsInfoEnum.Bitfinex.getOrgRestUrlSpot() + api, null,postData, createAuthHttpHeaders(plyload, null, aKey, sKey));
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result && null != result.get("id") && result.get("id").asText().length() > 3) {
                return ResultSet.build(ResultEnum.SUCCESS).setData(result.get("id").asText());
            } else {
                logger.error("bitfinex cancel 取消订单失败:{}", res);
                return  ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
            logger.error("bitfinex cancel 取消订单失败:{}", e.toString());
            return  ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    public static void main(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
    }


    public void spiderAccountData(long execTime) {
        String api = "/v1/balances";

        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for(Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if(config.getOrg() != OrgsInfoEnum.Bitfinex) continue;

                MapBuilder postData = MapBuilder.build()
                        .add("request", api)
                        .add("nonce", String.valueOf((nonce.incrementAndGet())));
                String plyload = JSONUtils.obj2json(postData);
                try {
                    String  res = HttpUtilManager.getInstance().requestHttpPost(
                            OrgsInfoEnum.Bitfinex.getOrgRestUrlSpot() + api, null,postData, createAuthHttpHeaders(plyload, config.getOrg(), config.getaKey(), config.getsKey()));
                    JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                    if(null != jsonNode && jsonNode.isArray()) {
                        AccountInfo accountInfo = new AccountInfo();
                        accountInfo.setType(AccountInfo.spot);
                        accountInfo.setRawData(jsonNode.toString());
                        accountInfo.setOrgName(config.getOrg().getOrgName());
                        accountInfo.setAccount(config.getAccount());
                        accountInfo.setaKey(config.getaKey());
                        accountInfo.setTime(execTime);

                        Map<String, Object> tmp = new HashMap<>();
                        for(JsonNode t : jsonNode) {
                            if(!"exchange".equals(t.get("type").asText())) continue;;
                            if(t.get("amount").asDouble() >0 || t.get("available").asDouble() >0 ) {
                                double locked = t.get("available").asDouble() - t.get("amount").asDouble();
                                tmp.put(t.get("currency").asText(), MapBuilder.build()
                                        .add(AccountInfo.locked_balance, locked)
                                        .add(AccountInfo.balance, t.get("available").asDouble()));
                            }
                        }
                        accountInfo.setBalanceInfo(tmp);
                        if(accountInfo.getBalanceInfo().keySet().size() == 0) return;
                        Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.spot);
                        mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                        mongoDbService.insert(accountInfo,  AccountInfo.collectionName);
                    }
                } catch (Exception e) {
                    logger.warn("bitfinex account 数据抓取失败:{}", e.toString());
                    spiderAccountData(execTime);
                }

            }
        }
    }


    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type).toUpperCase();
        if(null == tickers.get(type)) return new Ticker();
        Ticker ticker = tickers.get(type).getLast();
        return ticker;
    }


    //生成交易签名;返回 http header
    private static Map<String, String> createAuthHttpHeaders(String payload, OrgsInfoEnum orgsInfoEnum, String aKey, String sKey) {
        final String payloadBase64 = Base64.getEncoder().encodeToString(payload.getBytes());
        if(null == orgsInfoEnum) {
            orgsInfoEnum = OrgsInfoEnum.Bitfinex;
        }

        String payloadHmacSHA384 = createHmacSignature(sKey, payloadBase64, "HmacSHA384");
        Map<String, String> hdrs = new ConcurrentHashMap<>();
        hdrs.put("X-BFX-APIKEY", aKey);
        hdrs.put("X-BFX-PAYLOAD", payloadBase64);
        hdrs.put("X-BFX-SIGNATURE", payloadHmacSHA384);
        hdrs.put("Content-Type", "application/json");
        return hdrs;
    }

    //交易签名方法
    private static String createHmacSignature(String secret, String inputText, String algoName) {
        try {
            Mac mac = Mac.getInstance(algoName);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algoName);
            mac.init(key);

            return new String(Hex.encodeHex(mac.doFinal(inputText.getBytes(StandardCharsets.UTF_8))));

        } catch (Exception e) {
            throw new RuntimeException("cannot create " + algoName, e);
        }
    }
}
