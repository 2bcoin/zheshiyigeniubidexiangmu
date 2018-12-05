package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.neovisionaries.ws.client.*;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoinbaseProService extends BaseService {
    Logger logger = LoggerFactory.getLogger(CoinbaseProService.class);
    @Autowired
    private MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    //保存每种币每3秒钟的价格 只保存3600条数据
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public static String makeName(String type) {
        return  type
                .replace("_", "-")
                .replace("usdt", "usd")
                .toUpperCase();
    }

    public static void main(String []a) {
//      spiderMarketAll();

    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if(null != type) type =  makeName(type);

        DepthVo depthVo = depth.get(type);
        if(null == depthVo) return null;
        return depthVo;
    }

    //big1没有socket接口
    public void openSocket() {
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.CoinbasePro.getOrgSocketUrlSpot());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(CoinbaseProService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- CoinbasePro websocket连接成功[{}] ---------------------------", DateUtils.now(null));

                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.CoinbasePro)) {
                        websocket.sendText("{\"type\": \"subscribe\",\"product_ids\": [\"" + makeName(type) + "\"],\"channels\": [\"ticker\"]}");
                        websocket.sendText("{\"type\": \"subscribe\",\"product_ids\": [\"" + makeName(type) + "\"],\"channels\": [\"level2\"]}");
                    }

                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- CoinbasePro websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket();
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    JsonNode channel = jsonNode.get("type");
                    if(null != channel && ("snapshot".equals(channel.asText()) || "l2update".equals(channel.asText()))) {
                        String type = jsonNode.get("product_id").asText();
                        DepthVo depthVoMap = depth.get(makeName(type));
                        if(null == depthVoMap) depthVoMap = new DepthVo();

                        List<Depth> asks = depthVoMap.getAsks();
                        List<Depth> bids = depthVoMap.getBids();
                        if(null == asks) asks = ListBuilder.build();
                        if(null == bids) bids = ListBuilder.build();

                        if("snapshot".equals(channel.asText())) {
                            for(JsonNode t : jsonNode.get("bids")) {
                                bids.add(new Depth(t.get(0).asDouble(), t.get(1).asDouble()));
                            }
                            for(JsonNode t : jsonNode.get("asks")) {
                                asks.add(new Depth(t.get(0).asDouble(), t.get(1).asDouble()));
                            }
                            depthVoMap.setBids(bids);
                            depthVoMap.setAsks(asks);

                            depth.put(type, depthVoMap);
                        } else {
                            Map<String, Double> tmpMapAsks = new ConcurrentHashMap<>();
                            Map<String, Double> tmpMapBids = new ConcurrentHashMap<>();
                            for(Depth depth : asks) {
                                tmpMapAsks.put(String.valueOf(depth.getPrice()), depth.getQty());
                            }
                            for(Depth depth : bids) {
                                tmpMapBids.put(String.valueOf(depth.getPrice()), depth.getQty());
                            }

                            for(JsonNode t : jsonNode.get("changes")) {
                                if("buy".equals(t.get(0).asText())) {
                                    if(0 == t.get(2).asDouble()) {
                                        tmpMapBids.remove(String.valueOf(t.get(1).asDouble()));
                                    } else {
                                        tmpMapBids.put(String.valueOf(t.get(1).asDouble()), t.get(2).asDouble());
                                    }
                                } else {
                                    if(0 == t.get(2).asDouble()) {
                                        tmpMapAsks.remove(String.valueOf(t.get(1).asDouble()));
                                    } else {
                                        tmpMapAsks.put(String.valueOf(t.get(1).asDouble()), t.get(2).asDouble());
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
                            depth.put(makeName(type), depthVoMap);
                        }
    //                    if("BTC-USD".equals(type)) {
    //                        System.out.println(depthSpot.get("BTC-USD").getBids().get(0));
    //                    }
                    }
                    if(null != channel && "ticker".equals(channel.asText())) {
                        String name = jsonNode.get("product_id").asText();
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if (null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }
                        if (Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        secPrice.add(new Ticker(jsonNode.get("price").asText(), jsonNode.get("high_24h").asText(),jsonNode.get("low_24h").asText(),
                                jsonNode.get("best_bid").asText(),jsonNode.get("best_ask").asText(),jsonNode.get("volume_24h").asText()));
                        tickers.put(name, secPrice);
                    }
                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- CoinbasePro websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
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
        return null;
    }

    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type);
        if(null == tickers.get(type)) return new Ticker();
        Ticker ticker = tickers.get(type).getLast();
        return ticker;
    }



}
