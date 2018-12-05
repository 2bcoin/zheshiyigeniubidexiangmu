package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.libs.huobi.api.HuobiApiClient;
import com.github.misterchangray.libs.huobi.response.BalanceResponse;
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
public class HuoBiService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(HuoBiService.class);
    @Autowired
    ThreadPoolTaskExecutor executor;
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    //最近一次拉取賬號數據時間
    public static Long lastSpiderAccountData = null;
    private static String tradeUrl  = OrgsInfoEnum.HuoBi.getOrgRestUrlSpot().split("|")[0];//交易服务器
    private static String marketUrl = OrgsInfoEnum.HuoBi.getOrgRestUrlSpot().split("|")[1]; //行情服务

    public static String makeName(String type) {
        return type.replace("_", "").toLowerCase();
    }

    public void openSocket() {
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.HuoBi.getOrgSocketUrlSpot());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(HuoBiService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- 火币 websocket连接成功[{}] ---------------------------", DateUtils.now(null));

                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.HuoBi)) {
                        websocket.sendText("{\"sub\": \"market." + makeName(type) +".detail\",\"id\": \"ticker\"}");

                        websocket.sendText("{\"sub\": \"market." + makeName(type) +".depthSpot.step0\",\"id\": \"marketDepth\"}");
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- 火币 websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket();
                }


                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    super.onBinaryMessage(websocket, binary);
                    byte[] bytes = GZipUtils.uncompress(binary);
                    String jsonStr = new String(bytes, "utf8");

                    JsonNode jsonNode = JSONUtils.buildJsonNode(jsonStr);
                    if(null != jsonNode.get("ping")) {
                        websocket.sendText("{\"pong\":" + jsonNode.get("ping").asLong() +"}");
                        return;
                    }

                    if(null == jsonNode.get("ch")) return;

                    String name = jsonNode.get("ch").asText();
                    name = name.split("\\.")[1];
                    if(jsonNode.get("ch").asText().contains("detail")) {
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if(null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if(Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        secPrice.add(new Ticker(jsonNode.get("tick").get("close").asText(), jsonNode.get("tick").get("high").asText(), jsonNode.get("tick").get("low").asText(),
                                null, null, jsonNode.get("tick").get("vol").asText() ));
                        tickers.put(name,  secPrice);
                    }


                    if(jsonNode.get("ch").asText().contains("depthSpot")) {
                        DepthVo depthVoMap = depth.get(makeName(name));
                        if(null == depthVoMap) depthVoMap = new DepthVo();

                        ListBuilder cacheTmp = ListBuilder.build();
                        for(JsonNode tmp : jsonNode.get("tick").get("bids")) {
                            cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                        };
                        depthVoMap.setBids(cacheTmp);
                        cacheTmp = ListBuilder.build();
                        for(JsonNode tmp : jsonNode.get("tick").get("asks")) {
                            cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                        };
                        depthVoMap.setAsks(cacheTmp);
                        depthVoMap.setUpdateTime(System.currentTimeMillis());
                        depth.put(makeName(name), depthVoMap);

//                        logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                                name,
//                                depthVoMap.getBids().get(0).getPrice(),
//                                depthVoMap.getBids().get(0).getQty(),
//                                depthVoMap.getAsks().get(0).getPrice(),
//                                depthVoMap.getAsks().get(0).getQty());
                    }
                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- 火币 websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
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

    public static void main(String []ar) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对HTTP启用代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        // 对SOCKS开启代理
        System.setProperty("socks.proxyHost", proxyHost);
        System.setProperty("socks.proxyPort", proxyPort);
    }


    public void spiderAccountData(long execTime) {
        lastSpiderAccountData = execTime;
        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for(Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if(config.getOrg() != OrgsInfoEnum.HuoBi) continue;
                try {
                    // create HuobiApiClient using your api key and api secret:
                    HuobiApiClient client = new HuobiApiClient(config.getaKey(), config.getsKey());
                    // get symbol list:
                    AccountInfo accountInfo = new AccountInfo();
                    for(com.github.misterchangray.libs.huobi.response.Account account : client.getAccounts()) {
                        if("working".equals(account.state)) {
                            if(AccountInfo.otc.equals( account.type)) accountInfo.setType(AccountInfo.otc);
                            if(AccountInfo.spot.equals( account.type)) accountInfo.setType(AccountInfo.spot);

                            //{"id":"4254818","type":"spot","state":"working","userid":null,"list":[{"currency":"hb10","type":"order","balance":"0"},{"currency":"hb10","type":"frozen","balance":"0"}
                            BalanceResponse balanceResponse = client.balance(account.id + "");
                            accountInfo.setRawData(JSONUtils.obj2json(balanceResponse.getData()));
                            JsonNode jsonNode = JSONUtils.buildJsonNode(JSONUtils.obj2json(balanceResponse.getData()));

                            if(null != jsonNode.get("list") && jsonNode.get("list").isArray()) {
                                for(JsonNode tmp : jsonNode.get("list")) {
                                    if(0 < tmp.get("balance").asDouble()) {
                                        Object o2 = accountInfo.getBalanceInfo().get(tmp.get("currency").asText());
                                        MapBuilder m = MapBuilder.build();
                                        if(null != o2) m = (MapBuilder) o2;
                                        if("frozen".equals(tmp.get("type").asText())) {
                                            m.add(AccountInfo.locked_balance, tmp.get("balance").asDouble());
                                        } else if("trade".equals(tmp.get("type").asText())) {
                                            m.add(AccountInfo.balance, tmp.get("balance").asDouble());
                                        }
                                        accountInfo.getBalanceInfo().put(tmp.get("currency").asText(), m);
                                    }
                                }
                                accountInfo.setOrgName(config.getOrg().getOrgName());
                                accountInfo.setAccount(config.getAccount());
                                accountInfo.setaKey(config.getaKey());
                                accountInfo.setTime(execTime);

                            }
                        }
                    }
                    if(accountInfo.getBalanceInfo().keySet().size() == 0) return;
                    Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(accountInfo.getType());
                    mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                    mongoDbService.insert(accountInfo,  AccountInfo.collectionName);
                } catch (Exception e) {
                    logger.warn("huobi account 数据抓取失败:{}", e.toString());
                    spiderAccountData(execTime);
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
