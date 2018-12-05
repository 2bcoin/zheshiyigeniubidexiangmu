package com.github.misterchangray.service.platform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.libs.okcoin.rest.future.IFutureRestApi;
import com.github.misterchangray.libs.okcoin.rest.future.impl.FutureRestApiV1;
import com.github.misterchangray.libs.okcoin.rest.stock.IStockRestApi;
import com.github.misterchangray.libs.okcoin.rest.stock.impl.StockRestApi;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.platform.vo.*;
import com.github.misterchangray.service.po.AccountInfo;
import com.github.misterchangray.service.po.AccountBalance;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@Service
public class OKEXService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(OKEXService.class);
    @Autowired
    ThreadPoolTaskExecutor executor;
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String, ArrayDeque<Ticker>> tickersSpot = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String, DepthVo> depthSpot = MapBuilder.build();
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String, ArrayDeque<Ticker>> tickersOtc = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String, DepthVo> depthOtc = MapBuilder.build();
    //保存每种币K线信息 只保存3600条数据
    public static Map<String, ArrayDeque<Kline>> klineOtc = MapBuilder.build();

    public static String makeName(String type) {
        return type;
    }

    @Override
    public String customRequest(String method, String url, Map<String, String> params, String aKey, String sKey) {
        MapBuilder header = MapBuilder.build();
        header.add("ContentType", "application/x-www-form-urlencoded ");
        if (null == params) params = MapBuilder.build();
        params.put("api_key", aKey);
        params.put("sign", SignUtil.okexSign(sKey, params));

        String res = null;
        try {
            if ("GET".equalsIgnoreCase(method)) {
                res = HttpUtilManager.getInstance().request(method, url, params, null, header);
            } else {
                res = HttpUtilManager.getInstance().request(method, url, null, params, header);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public ResultSet<String> buy(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        IStockRestApi stockPost = new StockRestApi(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);
        String direc = null == price ? "buy_market" : "buy";
        try {
            String res = stockPost.trade(type.toString(), direc, null == price ? null : price.toString(), amount.toString());
            return ResultSet.build(ResultEnum.SUCCESS).setData(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public ResultSet<String> sell(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
//        限价单（buy/sell） 市价单（buy_market/sell_market）
        IStockRestApi stockPost = new StockRestApi(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);
        String direc = null == price ? "sell_market" : "sell";
        try {
            String res = stockPost.trade(type.toString(), direc, null == price ? null : price.toString(), amount.toString());
            return ResultSet.build(ResultEnum.SUCCESS).setData(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public List<Position> getPositionOtc(CoinOtc symbol, String contractType, String aKey, String sKey) {
        IFutureRestApi futurePostV1 = new FutureRestApiV1(OrgsInfoEnum.OKEX.getOrgRestUrlOtc(), aKey, sKey);

        try {
            String res = futurePostV1.future_position_4fix(symbol.toString() + "_usd", contractType);
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if (jsonNode.get("result").asBoolean()) {
                List<Position> positions = new ArrayList<>();

                for (JsonNode jsonNode1 : jsonNode.get("holding")) {
                    if (0 < jsonNode1.get("buy_amount").asInt()) {
                        Position position = new Position();
                        position.setInfo(jsonNode1);
                        position.setType(0);
                        position.setAmount(Utils.convertBigDecimal(jsonNode1.get("buy_amount").asInt()));
                        position.setContractType(jsonNode1.get("contract_type").asText());
                        position.setPrice(Utils.convertBigDecimal(jsonNode1.get("buy_price_avg").asDouble()));
                        position.setMarginLevel(Utils.convertBigDecimal(jsonNode1.get("lever_rate").asInt()));
                        position.setProfit(Utils.convertBigDecimal(jsonNode1.get("buy_profit_lossratio").asDouble()));
                        positions.add(position);
                    }
                    if (0 < jsonNode1.get("sell_amount").asInt()) {
                        Position position = new Position();
                        position.setInfo(jsonNode1);
                        position.setType(1);
                        position.setAmount(Utils.convertBigDecimal(jsonNode1.get("sell_amount").asInt()));
                        position.setContractType(jsonNode1.get("contract_type").asText());
                        position.setPrice(Utils.convertBigDecimal(jsonNode1.get("sell_price_avg").asDouble()));
                        position.setMarginLevel(Utils.convertBigDecimal(jsonNode1.get("lever_rate").asInt()));
                        position.setProfit(Utils.convertBigDecimal(jsonNode1.get("sell_profit_lossratio").asDouble()));
                        positions.add(position);
                    }
                }
                return positions;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * O~K
     *
     * @param symbol
     * @param aKey
     * @param sKey
     * @return
     */
    @Override
    public List<Order> getOrdersSpot(CoinSpot symbol, String aKey, String sKey) {

        IStockRestApi stockPost = new StockRestApi(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);

        try {
            String res = stockPost.order_info(symbol.toString(), "-1");
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if (jsonNode.get("result").asBoolean()) {
                List<Order> orderList = new ArrayList<>();
                for (JsonNode node : jsonNode.get("orders")) {
                    Order order = new Order();
                    order.setInfo(node);
                    order.setAmount(node.get("amount").asText());
                    order.setContractType(null);
                    order.setOrder_id(node.get("order_id").asText());
                    order.setPrice(node.get("price").asText());
                    order.setStatus(node.get("status").asText());
                    order.setSymbol(node.get("symbol").asText());
                    order.setSide(node.get("type").asText());
                    order.setType("1");
                    orderList.add(order);
                }
                for (Order order : orderList) {
                    System.out.println(order);
                }
                return orderList;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Order> getOrdersOtc(CoinOtc symbol, String contractType, String aKey, String sKey) {
        String url = OrgsInfoEnum.OKEX.getOrgRestUrlOtc();
        IFutureRestApi futurePostV1 = new FutureRestApiV1(url, aKey, sKey);

        try {
            String res = futurePostV1.future_order_info(symbol.toString() + "_usd", contractType, "-1",
                    "1", "1", "50");
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if (jsonNode.get("result").asBoolean()) {
                List<Order> orderList = new ArrayList<>();
                for (JsonNode node : jsonNode.get("orders")) {
                    Order order = new Order();
                    order.setInfo(node);
                    order.setSymbol(node.get("symbol").asText());
                    order.setLeverRate(node.get("lever_rate").asText());
                    order.setAmount(node.get("amount").asText());
                    order.setType("2");
                    order.setStatus(node.get("status").asText());
                    order.setOrder_id(node.get("order_id").asText());
                    order.setPrice(node.get("price").asText());
                    order.setFutureType(node.get("type").asText());
                    order.setContractType(contractType);
                    orderList.add(order);
                }
                return orderList;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public ResultSet<String> futureOrder(CoinOtc symbol, String contract_type, String direct, Integer lever_rate, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        String type = symbol.toString() + "_usd";
//        String[] types = {"btc_usd", "ltc_usd", "eth_usd", "etc_usd", "bch_usd", "btg_usd", "xrp_usd", "eos_usd"};
        String[] types = {"btc_usd", "eos_usd"};
        //  1:开多   2:开空   3:平多   4:平空
        Map<String, String> direts = new HashMap<String, String>() {{
            this.put("duo", "1");
            this.put("kong", "2");
            this.put("pingduo", "3");
            this.put("pingkong", "4");
        }};
        boolean flag = false;
        for (String t : types) {
            if (type.equals(t)) flag = true;
        }
        if (!flag) return ResultSet.build(ResultEnum.FAILURE).setMsg("交易币种错误");

        try {
            String url = OrgsInfoEnum.OKEX.getOrgRestUrlOtc();
            IFutureRestApi futurePostV1 = new FutureRestApiV1(url, aKey, sKey);

            String match_price = null == price ? "1" : "0";
            String res = futurePostV1.future_trade(type, contract_type, String.valueOf(price), String.valueOf(amount), direts.get(direct), match_price);
            JsonNode resJson = JSONUtils.buildJsonNode(res);
            if (null != res && resJson.get("result").asBoolean()) {
                return ResultSet.build(ResultEnum.SUCCESS).setData(resJson.get("order_id").asText());
            }
            return ResultSet.build(ResultEnum.FAILURE).setMsg(res);
        } catch (Exception e) {
            return ResultSet.build(ResultEnum.FAILURE);
        }
    }

    @Override
    public ResultSet<String> cancelOrder(String orderId, CoinSpot type, String aKey, String sKey) {
        IStockRestApi stockPost = new StockRestApi(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);
        try {
            String res = stockPost.cancel_order(type.toString(), orderId);
            JsonNode resJson = JSONUtils.buildJsonNode(res);
            if (null != res && resJson.get("result").asBoolean()) {
                return ResultSet.build(ResultEnum.SUCCESS);
            }
            return ResultSet.build(ResultEnum.FAILURE).setData(res);
        } catch (Exception e) {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public ResultSet<String> cancelOrderFuture(String orderId, CoinOtc type, String contractType, String aKey, String sKey) {
        IFutureRestApi futurePostV1 = new FutureRestApiV1(OrgsInfoEnum.OKEX.getOrgRestUrlOtc(), aKey, sKey);
        try {
            String res = futurePostV1.future_cancel(type.toString(), contractType, orderId);
            JsonNode resJson = JSONUtils.buildJsonNode(res);
            if (null != res && resJson.get("result").asBoolean()) {
                return ResultSet.build(ResultEnum.SUCCESS);
            }
            return ResultSet.build(ResultEnum.FAILURE).setData(res);
        } catch (Exception e) {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if (null != type) type = makeName(type);

        DepthVo depthVo = depthSpot.get(type);
        if (null == depthVo) throw new RuntimeException(MessageFormat.format("okex 现货没有 {0} 的深度数据", type));
        return depthVo;
    }


    @Override
    public DepthVo getDepthOtc(CoinOtc type, String contract_type) {
        String key = null;
        key = MessageFormat.format("ok_sub_futureusd_{0}_depth_{1}_20", type.toString(), contract_type);
        if (null == key) return null;

        DepthVo depthVo = depthOtc.get(key);
        if(null == depthVo) throw new RuntimeException(MessageFormat.format("okex 期货没有 {0} 的深度数据", key));
        return depthVo;
    }

    @Override
    public Ticker getTickerOtc(CoinOtc type, String contract_type) {
        IFutureRestApi futurePostV1 = new FutureRestApiV1(OrgsInfoEnum.OKEX.getOrgRestUrlSpot());
        String type2 = type.toString() + "_usd";
        try {
            String res = futurePostV1.future_ticker(type2, contract_type);
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if (null != jsonNode.get("ticker")) {
                Ticker ticker = new Ticker();
                ticker.setInfo(jsonNode);
                ticker.setUpdateTime(System.currentTimeMillis());
                ticker.setLast(jsonNode.get("ticker").get("last").asText());
                ticker.setVol(jsonNode.get("ticker").get("vol").asText());
                ticker.setLow(jsonNode.get("ticker").get("low").asText());
                ticker.setHigh(jsonNode.get("ticker").get("high").asText());
                ticker.setBuy(jsonNode.get("ticker").get("buy").asText());
                ticker.setSell(jsonNode.get("ticker").get("sell").asText());
                return ticker;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Kline> getRecordsOtc(CoinOtc type, String contract_type, String cycle, Long size) {
        //btc_usd ltc_usd eth_usd etc_usd bch_usd;
        String type2 = type.toString() + "_usd";
        String url = OrgsInfoEnum.OKEX.getOrgRestUrlOtc() + "/api/v1/future_kline.do";
        try {
            String res = HttpUtilManager.getInstance().requestHttpGet(url, MapBuilder.build()
                    .add("symbol", type2)
                    .add("contract_type", contract_type)
                    .add("type", cycle)
                    .add("size", size), null);
            if (null != res) {
                List<Kline> klines = new ArrayList<>();

                JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                for (JsonNode tmp : jsonNode) {
                    Kline kline = new Kline(tmp.get(0).asLong(), tmp.get(1).asDouble(), tmp.get(4).asDouble(),
                            tmp.get(3).asDouble(), tmp.get(2).asDouble(), tmp.get(5).asDouble());
                    kline.setInfo(tmp);
                    klines.add(kline);
                }
                return klines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Kline> getRecordsSpot(CoinSpot type, String cycle, Long size) {
        //btc_usd ltc_usd eth_usd etc_usd bch_usd;
        String url = OrgsInfoEnum.OKEX.getOrgRestUrlSpot() + "/api/v1/kline.do";
        try {
            String res = HttpUtilManager.getInstance().requestHttpGet(url, MapBuilder.build()
                    .add("symbol", type.toString())
                    .add("type", cycle)
                    .add("size", size), null);
            if (null != res) {
                List<Kline> klines = new ArrayList<>();

                JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                for (JsonNode tmp : jsonNode) {
                    Kline kline = new Kline(tmp.get(0).asLong(), tmp.get(1).asDouble(), tmp.get(4).asDouble(),
                            tmp.get(3).asDouble(), tmp.get(2).asDouble(), tmp.get(5).asDouble());
                    kline.setInfo(tmp);
                    klines.add(kline);
                }
                return klines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资产转换,用于总共转换成统一得资产
     *
     * @param type   用于转换得币种
     * @param amount 改币得数量
     * @param to     转为为什么价格, 只能为 btc 或者 usdt
     * @return
     */
    @Override
    public Double convertAsset(String type, Double amount, String to) {
        if (to.equalsIgnoreCase("btc")) {
            if (type.equalsIgnoreCase("btc")) return amount;
            Ticker ticker = this.getTickerSpot(type + "_btc");
            if (null != ticker && null != ticker.getLast()) {
                return amount * Double.parseDouble(ticker.getLast());
            }
            return 0d;
        } else {
            if (type.equalsIgnoreCase("usdt")) return amount;
            Ticker ticker = this.getTickerSpot(type + "_usdt");
            if (null != ticker && null != ticker.getLast()) {
                return amount * Double.parseDouble(ticker.getLast());
            }
            return 0d;
        }
    }


    @Override
    public List<AccountBalance> getAccount(String aKey, String sKey) {
        List<AccountBalance> accountBalances = new ArrayList<>();

        IFutureRestApi futurePostV1 = new FutureRestApiV1(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);
        IStockRestApi stockPost = new StockRestApi(OrgsInfoEnum.OKEX.getOrgRestUrlSpot(), aKey, sKey);

        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setTime(System.currentTimeMillis());
        accountBalance.setaKey(aKey);
        accountBalance.setOrgName(OrgsInfoEnum.OKEX.getOrgName());
        accountBalance.setType(AccountBalance.otc);
        try {
            JsonNode userInfo = JSONUtils.buildJsonNode(futurePostV1.future_userinfo_4fix());
            accountBalance.setInfo(userInfo);
            if (null != userInfo && null != userInfo.get("result") && userInfo.get("result").asBoolean()) {
                JsonNode parent = userInfo.get("info");
                Iterator<String> tmp = parent.fieldNames();
                BigDecimal totalUsd = new BigDecimal(0);
                BigDecimal totalBtc = new BigDecimal(0);

                while (tmp.hasNext()) {
                    String coinName = tmp.next();
                    if (0 != parent.get(coinName).get("balance").asDouble() || 0 != parent.get(coinName).get("contracts").size()) {
                        totalUsd = BigDecimalUtils.add(Utils.convertBigDecimal(convertAsset(coinName, parent.get(coinName).get("rights").asDouble(), "USDT")), totalUsd);
                        totalBtc = BigDecimalUtils.add(Utils.convertBigDecimal(convertAsset(coinName, parent.get(coinName).get("rights").asDouble(), "BTC")), totalBtc);
                    }
                }
                accountBalance.setBalance_btc(totalBtc);
                accountBalance.setBalance_usd(totalUsd);
                accountBalances.add(accountBalance);
            }

            accountBalance = new AccountBalance();
            accountBalance.setTime(System.currentTimeMillis());
            accountBalance.setaKey(aKey);
            accountBalance.setOrgName(OrgsInfoEnum.OKEX.getOrgName());
            accountBalance.setType(AccountBalance.spot);
            String userInfoSpot = stockPost.userinfo();
            userInfo = JSONUtils.buildJsonNode(userInfoSpot);
            accountBalance.setInfo(userInfo);

            if (null != userInfo && null != userInfo.get("result") && userInfo.get("result").asBoolean()) {
                JsonNode parent = userInfo.get("info").get("funds").get("free");
                JsonNode parent2 = userInfo.get("info").get("funds").get("freezed");
                Iterator<String> tmp = parent.fieldNames();

                BigDecimal totalUsd = new BigDecimal(0);
                BigDecimal totalBtc = new BigDecimal(0);
                while (tmp.hasNext()) {
                    String coinName = tmp.next();
                    Double freezed = JSONUtils.getJsonPathVal(parent2, coinName, 0).asDouble();

                    if (0 != parent.get(coinName).asDouble() || 0 != freezed) {
                        BigDecimal bigDecimal = Utils.convertBigDecimal(freezed + parent.get(coinName).asDouble());
                        totalUsd = BigDecimalUtils.add(convertAsset(coinName, bigDecimal.doubleValue(), "USDT"), totalUsd.doubleValue());
                        totalBtc = BigDecimalUtils.add(convertAsset(coinName, bigDecimal.doubleValue(), "BTC"), totalBtc.doubleValue());
                    }
                }
                accountBalances.add(accountBalance);
            }
        } catch (Exception e) {
            logger.warn("okex account 数据抓取失败:{}", e.toString());
        }
        return accountBalances;
    }


    public void openSocketOtc() {
        String[] types = CoinOtc.getCoins();
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.OKEX.getOrgSocketUrlOtc());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(OKEXService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- OKEX otc websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                    for (String type : types) {
                        ws.sendText("{'event':'addChannel','channel':'ok_sub_futureusd_" + type + "_depth_this_week_20'}");
                        ws.sendText("{'event':'addChannel','channel':'ok_sub_futureusd_" + type + "_depth_next_week_20'}");
                        ws.sendText("{'event':'addChannel','channel':'ok_sub_futureusd_" + type + "_depth_quarter_20'}");
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- OKEX otc websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickersOtc = MapBuilder.build();
                    depthOtc = MapBuilder.build();
                    klineOtc = MapBuilder.build();
                    openSocketSpot();
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    String text = new String(uncompress(binary));

                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    if (jsonNode.isArray()) {

                        jsonNode.forEach(item -> {
                            String name = item.get("channel").asText();

                            if (name.contains("_ticker")) {
                                ArrayDeque<Ticker> secPrice = tickersSpot.get(name);
                                if (null == secPrice) {
                                    secPrice = new ArrayDeque(Const.maxCacheTypes);
                                }

                                if (Const.maxCacheTypes < secPrice.size()) {
                                    secPrice.removeFirst();
                                }
                                secPrice.add(new Ticker(item.get("data").get("last").asText(), item.get("data").get("high").asText(),
                                        item.get("data").get("low").asText(), item.get("data").get("buy").asText(),
                                        item.get("data").get("sell").asText(), item.get("data").get("vol").asText()));
                                tickersOtc.put(name, secPrice);
                            }

                            if (name.contains("_kline")) {
                                ArrayDeque<Kline> secPrice = klineOtc.get(name);
                                if (null == secPrice) {
                                    secPrice = new ArrayDeque(Const.maxCacheTypes);
                                }

                                if (Const.maxCacheTypes < secPrice.size()) {
                                    secPrice.removeFirst();
                                }
                                secPrice.add(new Kline(item.get("data").get(0).get(0).asLong(),
                                        item.get("data").get(0).get(1).asDouble(), item.get("data").get(0).get(4).asDouble(),
                                        item.get("data").get(0).get(3).asDouble(), item.get("data").get(0).get(2).asDouble(),
                                        item.get("data").get(0).get(5).asDouble()));
                                klineOtc.put(name, secPrice);
                            }

                            if (name.contains("_depth")) {
                                DepthVo depthVoMap = depthSpot.get(name);
                                if (null == depthVoMap) depthVoMap = new DepthVo();

                                ListBuilder cacheTmp = ListBuilder.build();
                                for (JsonNode tmp : item.get("data").get("bids")) {
                                    cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                                }
                                ;
                                depthVoMap.setBids(cacheTmp);
                                cacheTmp = ListBuilder.build();
                                for (JsonNode tmp : item.get("data").get("asks")) {
                                    cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                                }
                                ;
                                depthVoMap.setAsks(cacheTmp);
                                depthVoMap.setUpdateTime(System.currentTimeMillis());
                                depthOtc.put(makeName(name), depthVoMap);


//                                if("ok_sub_futureusd_btc_depth_quarter_20".equals(name)) {
//                                    System.out.println(depthVoMap.getAsks().get(0).getPrice() + ", " + depthVoMap.getAsks().get(0).getQty());
////                                        System.out.println(depthVoMap.getBids().get(0).getPrice() + ", " + depthVoMap.getBids().get(0).getQty());
//                                }

                            }
                        });
                    }
                }


            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- okex otc websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocketSpot();
        }
    }



    private static String uncompress(byte[] bytes) {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             final Deflate64CompressorInputStream zin = new Deflate64CompressorInputStream(in)) {
            final byte[] buffer = new byte[1024];
            int offset;
            while (-1 != (offset = zin.read(buffer))) {
                out.write(buffer, 0, offset);
            }
            return out.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void openSocketSpot() {
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.OKEX.getOrgSocketUrlSpot());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(OKEXService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- OKEX websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.OKEX)) {
                        ws.sendText("{'event':'addChannel','channel':'ok_sub_spot_" + makeName(type) + "_ticker'}");

                        ws.sendText("{'event':'addChannel','channel':'ok_sub_spot_" + makeName(type) + "_depth_20'}");
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- OKEX websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickersSpot = MapBuilder.build();
                    depthSpot = MapBuilder.build();
                    openSocketSpot();
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    String text = new String(uncompress(binary));
                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    if (jsonNode.isArray()) {
                        jsonNode.forEach(item -> {
                            String name = item.get("channel").asText();
                            name = name.replace("ok_sub_spot_", "");
                            if (name.contains("_ticker")) {
                                name = name.replace("_ticker", "");
                                ArrayDeque<Ticker> secPrice = tickersSpot.get(name);
                                if (null == secPrice) {
                                    secPrice = new ArrayDeque(Const.maxCacheTypes);
                                }

                                if (Const.maxCacheTypes < secPrice.size()) {
                                    secPrice.removeFirst();
                                }
                                secPrice.add(new Ticker(item.get("data").get("last").asText(), item.get("data").get("high").asText(),
                                        item.get("data").get("low").asText(), item.get("data").get("buy").asText(),
                                        item.get("data").get("sell").asText(), item.get("data").get("vol").asText()));
                                tickersSpot.put(name, secPrice);
                            }


                            if (name.contains("_depth_20")) {
                                name = name.replace("_depth_20", "");

                                DepthVo depthVoMap = depthSpot.get(makeName(name));
                                if (null == depthVoMap) depthVoMap = new DepthVo();

                                ListBuilder cacheTmp = ListBuilder.build();
                                for (JsonNode tmp : item.get("data").get("bids")) {
                                    cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                                }
                                ;
                                depthVoMap.setBids(cacheTmp);
                                cacheTmp = ListBuilder.build();
                                for (JsonNode tmp : item.get("data").get("asks")) {
                                    cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                                }
                                ;
                                depthVoMap.setAsks(cacheTmp);
                                depthVoMap.setUpdateTime(System.currentTimeMillis());
                                depthSpot.put(makeName(name), depthVoMap);

//                                logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                                        name,
//                                        depthVoMap.getBids().get(1).getPrice(),
//                                        depthVoMap.getBids().get(1).getQty(),
//                                        depthVoMap.getAsks().get(1).getPrice(),
//                                        depthVoMap.getAsks().get(1).getQty());
                            }
                        });
                    }
                }

            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- okex websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocketSpot();
        }
    }

    @Override
    public Boolean init() {
        openSocketSpot();
        openSocketOtc();
        return super.init();
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        return null;
    }

    public static void main(String[] a) {
        //对apache httpclient 使用代理
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));

        //对websocket启用代理
        ProxySettings settings = Init.webSocketFactory.getProxySettings();
        settings.setHost(proxyHost);
        settings.setPort(Integer.parseInt(proxyPort));
        //对HTTP启用代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        // 对SOCKS开启代理
        System.setProperty("socks.proxyHost", proxyHost);
        System.setProperty("socks.proxyPort", proxyPort);


        new OKEXService().init();

    }

    public void spiderAccountData(long execTime) {
        HttpsURLConnection conn = null;

        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for (Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if (config.getOrg() != OrgsInfoEnum.OKEX) continue;

                IFutureRestApi futurePostV1 = new FutureRestApiV1(config.getOrg().getOrgRestUrlSpot(), config.getaKey(), config.getsKey());
                IStockRestApi stockPost = new StockRestApi(config.getOrg().getOrgRestUrlSpot(), config.getaKey(), config.getsKey());

                AccountInfo accountInfo = new AccountInfo();
                try {
                    String future_userinfo = futurePostV1.future_userinfo_4fix();
                    accountInfo.setRawData(future_userinfo);
                    JsonNode userInfo = JSONUtils.buildJsonNode(future_userinfo);
                    if (null != userInfo && null != userInfo.get("result") && userInfo.get("result").asBoolean()) {
                        JsonNode parent = userInfo.get("info");
                        Iterator<String> tmp = parent.fieldNames();

                        while (tmp.hasNext()) {
                            String coinName = tmp.next();
                            if (0 != parent.get(coinName).get("balance").asDouble() || 0 != parent.get(coinName).get("contracts").size()) {
                                accountInfo.getBalanceInfo().put(coinName, MapBuilder.build()
//                                        .add(AccountInfo.balance, parent.get(coinName).get("balance").asDouble() + parent.get(coinName).get("rights").asDouble())
                                                .add(AccountInfo.balance, parent.get(coinName).get("rights").asDouble())
                                                .add(AccountInfo.trading, parent.get(coinName).get("contracts").toString())
                                );
                            }
                        }
                        accountInfo.setType(AccountInfo.otc);
                        accountInfo.setOrgName(config.getOrg().getOrgName());
                        accountInfo.setAccount(config.getAccount());
                        accountInfo.setaKey(config.getaKey());
                        accountInfo.setTime(execTime);

                        if (accountInfo.getBalanceInfo().keySet().size() != 0) {
                            Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.otc);
                            mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                            mongoDbService.insert(accountInfo, AccountInfo.collectionName);
                        }
                        ;
                    }


                    accountInfo = new AccountInfo();
                    String userInfoSpot = stockPost.userinfo();
                    userInfo = JSONUtils.buildJsonNode(userInfoSpot);
                    accountInfo.setRawData(userInfoSpot);
                    if (null != userInfo && null != userInfo.get("result") && userInfo.get("result").asBoolean()) {
                        JsonNode parent = userInfo.get("info").get("funds").get("free");
                        JsonNode parent2 = userInfo.get("info").get("funds").get("freezed");
                        Iterator<String> tmp = parent.fieldNames();

                        while (tmp.hasNext()) {
                            String coinName = tmp.next();
                            Double freezed = JSONUtils.getJsonPathVal(parent2, coinName, 0).asDouble();

                            if (0 != parent.get(coinName).asDouble() || 0 != freezed) {
                                accountInfo.getBalanceInfo().put(coinName, MapBuilder.build()
                                        .add(AccountInfo.balance, parent.get(coinName).asDouble())
                                        .add(AccountInfo.locked_balance, freezed)
                                );
                            }
                        }
                        accountInfo.setType(AccountInfo.spot);
                        accountInfo.setOrgName(config.getOrg().getOrgName());
                        accountInfo.setAccount(config.getAccount());
                        accountInfo.setaKey(config.getaKey());
                        accountInfo.setTime(execTime);

                        if (accountInfo.getBalanceInfo().keySet().size() != 0) {
                            Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.spot);
                            mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                            mongoDbService.insert(accountInfo, AccountInfo.collectionName);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("okex account 数据抓取失败:{}", e.toString());
                    spiderAccountData(execTime);
                }
            }
        }
    }

    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type);
        if (null == tickersSpot.get(type)) return new Ticker();
        Ticker ticker = tickersSpot.get(type).getLast();
        return ticker;
    }

}
