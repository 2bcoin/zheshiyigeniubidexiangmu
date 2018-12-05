package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
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
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;


@Service
public class OTCBTCService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(OTCBTCService.class);

    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public String makeName(String type) {
        return type.replace("_", "");
    }

    public void openSocket() {
        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");

        Pusher pusher = new Pusher("8cf6d4a8a620ca268cee", options);
        pusher.getConnection().bind(ConnectionState.CONNECTED, new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                if(connectionStateChange.getCurrentState().equals(ConnectionState.CONNECTED)){
                    logger.info("--------------------------- OTCBTC websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                }
            }
            @Override
            public void onError(String s, String s1, Exception e) {
                logger.info("--------------------------- OTCBTC websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                //连接断开后清空所有数据
                tickers = MapBuilder.build();
                depth = MapBuilder.build();
                openSocket();
            }
        });
        pusher.connect();
        Channel channel = pusher.subscribe("market-global");
        channel.bind("tickersSpot", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                JsonNode jsonNode = JSONUtils.buildJsonNode(data);

                for (String type : Const.orgsTypes.get(OrgsInfoEnum.OTCBTC)) {
                    String name = makeName(type);
                    JsonNode ticker = jsonNode.get(makeName(name));

                    if(null != ticker) {
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if(null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if(Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        secPrice.add(new Ticker(ticker.get("last").asText(),ticker.get("high").asText(),ticker.get("low").asText(),
                                ticker.get("buy").asText(),ticker.get("sell").asText(),ticker.get("volume").asText()));
                        tickers.put(name,  secPrice);
                    }
                }


            }
        });

        Channel marketChannel = null;
        for (String type : Const.orgsTypes.get(OrgsInfoEnum.OTCBTC)) {
            //订阅所有得市场交易信息
            marketChannel = pusher.subscribe("market-" + makeName(type) + "-global");
            marketChannel.bind("update", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                    String name = channelName;

                    JsonNode jsonNode = JSONUtils.buildJsonNode(data);
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


//                    logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                            name,
//                            depthVoMap.getBids().get(1).getPrice(),
//                            depthVoMap.getBids().get(1).getQty(),
//                            depthVoMap.getAsks().get(1).getPrice(),
//                            depthVoMap.getAsks().get(1).getQty());
                }
            });
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
    public DepthVo getDepthSpot(String type) {
        if(null != type) type =  makeName(type);

        DepthVo depthVo = depth.get(type);
        if(null == depthVo) return null;
        Long tmp = System.currentTimeMillis() - depthVo.getUpdateTime();
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
