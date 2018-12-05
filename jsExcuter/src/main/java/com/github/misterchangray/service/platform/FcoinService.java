package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
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
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;


@Service
public class FcoinService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;
    Logger logger = LoggerFactory.getLogger(FcoinService.class);
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public static   String makeName(String type) {
        return type.replace("_", "").toLowerCase();
    }

    public void openSocket() {

        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.Fcoin.getOrgSocketUrlSpot());


            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(FcoinService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- fcoin websocket连接成功[{}] ---------------------------", DateUtils.now(null));
//                    websocket.sendText("{\"cmd\": \"ticker.btcusdt\", \"args\": \"ticker.btcusdt\", \"id\": \"1\"}");
                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.Fcoin)) {
                        websocket.sendText("{\"cmd\":\"sub\",\"args\":[\"ticker." + makeName(type) + "\"],\"id\":\"1\"}");

                        websocket.sendText("{\"cmd\":\"sub\",\"args\":[\"" + String.format("depthSpot.L20.%s", makeName(type))+"\"],\"id\":\"2\"}");
                    }
                }
                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- fcoin websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket();
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);

                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    String name = jsonNode.get("type").asText();
                    if(name.contains("ticker")) {
                        name = makeName(name.split("\\.")[1]);
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if (null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if (Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        /**
                         *
                         * {"ticker":[6617.230000000,0.120400000,6617.220000000,0.050000000,6617.230000000,0.267900000,6350.780000000,6669.000000000,6334.660000000,180762.671977941,1156820006.673148064270000000],"type":"ticker.btcusdt","seq":141148350}
                         * [
                         *   "最新成交价",
                         *   "最近一笔成交的成交量",
                         *   "最大买一价",
                         *   "最大买一量",
                         *   "最小卖一价",
                         *   "最小卖一量",
                         *   "24小时前成交价",
                         *   "24小时内最高价",
                         *   "24小时内最低价",
                         *   "24小时内基准货币成交量, 如 btcusdt 中 btc 的量",
                         *   "24小时内计价货币成交量, 如 btcusdt 中 usdt 的量"
                         * ]
                         */
                        secPrice.add(new Ticker(jsonNode.get("ticker").get(0).asText(),jsonNode.get("ticker").get(7).asText(),jsonNode.get("ticker").get(8).asText(),
                                jsonNode.get("ticker").get(2).asText(),jsonNode.get("ticker").get(4).asText(),null));
                        tickers.put(name, secPrice);
                    }

                    if(name.contains("depthSpot")) {
                        name = makeName(name.split("\\.")[2]);
                        DepthVo depthVoMap = depth.get(makeName(name));
                        if(null == depthVoMap) depthVoMap = new DepthVo();

                        ListBuilder cacheTmp = ListBuilder.build();
                        JsonNode jsonNodeBids = jsonNode.get("bids");
                        for(int i=0, j=jsonNodeBids.size(); i<j; i++) {
                            if(1 == i%2) continue;
                            cacheTmp.append(new Depth(jsonNodeBids.get(i).asDouble(), jsonNodeBids.get(i+1).asDouble()));
                        };
                        depthVoMap.setBids(cacheTmp);
                        cacheTmp = ListBuilder.build();
                        jsonNodeBids = jsonNode.get("asks");
                        for(int i=0, j=jsonNodeBids.size(); i<j; i++) {
                            if(1 == i%2) continue;
                            cacheTmp.append(new Depth(jsonNodeBids.get(i).asDouble(), jsonNodeBids.get(i+1).asDouble()));
                        };
                        depthVoMap.setAsks(cacheTmp);
                        depthVoMap.setUpdateTime(System.currentTimeMillis());
                        depth.put(makeName(name), depthVoMap);

//                        logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                        name,
//                        depthVoMap.getBids().get(0).getPrice(),
//                        depthVoMap.getBids().get(0).getQty(),
//                        depthVoMap.getAsks().get(0).getPrice(),
//                        depthVoMap.getAsks().get(0).getQty());
                    }

                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- fcoin websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocket();
        }
    }

    @Override
    public Boolean init() {
//        openSocket();
        return true;
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {

        return null;
    }

    public static void main(String [] a) {
    }


    public  void spiderAccountData(long execTime) {
        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for(Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if(config.getOrg() != OrgsInfoEnum.Fcoin) continue;

                String res = null;
                AccountInfo accountInfo = new AccountInfo();
                Long time = System.currentTimeMillis();
                String url = config.getOrg().getOrgRestUrlSpot() + "accounts/balance";
                String signerStr =CryptoUtils.encodeBASE64( CryptoUtils.hamcsha1(CryptoUtils.encodeBASE64("GET" + url + time).getBytes(), config.getsKey().getBytes()));
                try {
                    // headers: {
                    //                'FC-ACCESS-KEY': this.config.key,
                    //                'FC-ACCESS-SIGNATURE': signtmp,
                    //                'FC-ACCESS-TIMESTAMP': time
                    //            }
                    res = HttpUtilManager.getInstance().requestHttpGet(url,  null,
                            MapBuilder.build()
                                    .add("Connection", "keep-alive")
                                    .add("FC-ACCESS-KEY", config.getaKey())
                                    .add("FC-ACCESS-SIGNATURE",signerStr)
                                    .add("FC-ACCESS-TIMESTAMP", time + "")
                    );
                    accountInfo.setRawData(res);
                    //{"status":0,"data":[{"currency":"abt","category":"fone::roaming","available":"0.000000000000000000","frozen":"0.000000000000000000","balance":"0.000000000000000000"},{"currency":"uuu","category":"fone::blockvc","available":"0.000000000000000000","frozen":"0.000000000000000000","balance":"0.000000000000000000"}]}
                    JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                    if(null != res && null != jsonNode && 0 == jsonNode.get("status").asInt()) {
                        for(JsonNode tmp : jsonNode.get("data")) {
                            if(0 < tmp.get("available").asDouble() || 0 < tmp.get("frozen").asDouble()) {
                                accountInfo.getBalanceInfo().put(tmp.get("currency").asText(), MapBuilder.build()
                                        .add(AccountInfo.balance, tmp.get("available").asDouble())
                                        .add(AccountInfo.locked_balance,  tmp.get("frozen").asDouble())
                                );
                            }
                        }

                        accountInfo.setOrgName(config.getOrg().getOrgName());
                        accountInfo.setAccount(config.getAccount());
                        accountInfo.setaKey(config.getaKey());
                        accountInfo.setType(AccountInfo.spot);
                        accountInfo.setTime(execTime);
                        if(accountInfo.getBalanceInfo().keySet().size() == 0) return;
                        Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.spot);
                        mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                        mongoDbService.insert(accountInfo,  AccountInfo.collectionName);
                    }
                } catch (Exception e) {
                    logger.warn("fcoin account 数据抓取失败:{}", e.toString());
                    spiderAccountData(execTime);
//            e.printStackTrace(); TODO
                }
            }
        }

    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if(null != type) type =  makeName(type);

        DepthVo depthVo = depth.get(type);
        if(null == depthVo) return null;
        return depthVo;
    }

    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type);
        if(null == tickers.get(type)) return new Ticker();
        Ticker ticker = tickers.get(type).getLast();
        return ticker;
    }

}
