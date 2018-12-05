package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.ListBuilder;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

//zb网
@Service
public class ZbSiteService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(ZbSiteService.class);
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public  String makeName(String type) {
        return type.replace("_", "");
    }

    public void openSocket() {

        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.Zb_site.getOrgSocketUrlSpot());
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(BitfinexService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- zb网 websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.Zb_site)) {
                        websocket.sendText("{'event':'addChannel','channel': '" + makeName(type) + "_ticker" +"',}");

                        websocket.sendText("{'event':'addChannel','channel': '" + makeName(type) + "_depth" + "',}");
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- zb网 websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket();
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);

                    //{"date":"1531796225117","channel":"btcusdt_ticker","dataType":"ticker","ticker":{"vol":"16531.4196","high":"6757.69","low":"6329.4","last":"6737.25","buy":"6739.06","sell":"6740.23"}}
                    //订阅成功后返回得数据

                    String name = jsonNode.get("channel").asText();
                    if(name.contains("_ticker")) {
                        if(null != jsonNode.get("no") &&
                                0 == jsonNode.get("no").asInt()) return;;

                        name = name.replace("_ticker", "");
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if(null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if(Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        secPrice.add(new Ticker(jsonNode.get("ticker").get("last").asText(),jsonNode.get("ticker").get("high").asText(),
                                jsonNode.get("ticker").get("low").asText(),jsonNode.get("ticker").get("buy").asText(),
                                jsonNode.get("ticker").get("sell").asText(),jsonNode.get("ticker").get("vol").asText()));
                        tickers.put(name,  secPrice);
                    }

                    if(name.contains("_depth")) {
                        name = name.replace("_depth", "");

                        DepthVo depthVoMap = depth.get(makeName(name));
                        if(null == depthVoMap) depthVoMap = new DepthVo();

                        ListBuilder cacheTmp = ListBuilder.build();
                        for(JsonNode tmp : jsonNode.get("bids")) {
                            cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                        };
                        depthVoMap.setBids(cacheTmp);
                        cacheTmp = ListBuilder.build();
                        for(JsonNode tmp : jsonNode.get("asks")) {
                            cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                        };
                        depthVoMap.setAsks(cacheTmp);
                        depthVoMap.setUpdateTime(System.currentTimeMillis());
                        depth.put(makeName(name), depthVoMap);

//                        logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                                name,
//                                depthVoMap.getBids().get(1).getPrice(),
//                                depthVoMap.getBids().get(1).getQty(),
//                                depthVoMap.getAsks().get(1).getPrice(),
//                                depthVoMap.getAsks().get(1).getQty());
                    }

                }
            });
            ws.connect();
        }  catch (Exception e) {
            logger.warn("--------------------------- zb网 websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
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
