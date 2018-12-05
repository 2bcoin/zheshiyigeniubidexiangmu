package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class BitzService extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    Logger logger = LoggerFactory.getLogger(BitzService.class);
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    private static String baseUrl = OrgsInfoEnum.Bit_z.getOrgRestUrlSpot();


    public String makeName(String type) {
        return type;
    }
    public String deMakeName(String type) {
        return type;
    }

    public  void spiderTicker() {
        String res = null;
        //如果距离上次抓取不足1S 则不重复抓取数据
        for(String key : tickers.keySet()) {
            long timeSub = System.currentTimeMillis() - tickers.get(key).getLast().getUpdateTime();
            if(timeSub < 1000) return;
        }

        try {
            res = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/Market/tickerall", null, MapBuilder.build().add("Connection", "keep-alive"));
            if("".equals(res) || null == res) return;
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if(null == jsonNode) return;
            if(200 != jsonNode.get("status").asInt()) {
                logger.error(jsonNode.toString());
                return;
            }
            jsonNode = jsonNode.get("data");
            if(null != jsonNode && jsonNode.isObject()) {
                for (String type : Const.orgsTypes.get(OrgsInfoEnum.Bit_z)) {
                    String name = makeName(type);
                    if(null != jsonNode.get(type)) {
                        ArrayDeque<Ticker> secPrice = tickers.get(name);
                        if(null == secPrice) {
                            secPrice = new ArrayDeque(Const.maxCacheTypes);
                        }

                        if(Const.maxCacheTypes < secPrice.size()) {
                            secPrice.removeFirst();
                        }
                        JsonNode tmp = jsonNode.get(type);
                        secPrice.add(new Ticker(tmp.get("now").asText(), tmp.get("high").asText(), tmp.get("low").asText(),
                                tmp.get("bidPrice").asText(), tmp.get("askPrice").asText(), tmp.get("volume").asText()));
                        tickers.put(name,  secPrice);
                    }
                }
            }
        } catch (Exception e) {
//            logger.warn("bitz ticker 数据抓取失败:{}", e.toString());
//            e.printStackTrace();
        }
    }

    @Override
    public Boolean init() {
//        openSocket();
        return true;
    }

    @Override
    //bit_z没有socket接口
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {

        return null;
    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if(null != type) type =  makeName(type);

        DepthVo depthVo = depth.get(type);
        if(null == depthVo) return null;
        Long tmp = System.currentTimeMillis() - depthVo.getUpdateTime();
        //超出1s的数据被认为无效
        if(tmp < 1000) return depthVo;
        return null;

    }

    //移出没用的交易对
    public static void removeInvalidCoin(String coin) {
        List<String> s = Const.orgsTypes.get(OrgsInfoEnum.Bit_z);
        List<String> tmp = new ArrayList();
        for(String key : s) {
            if(!key .equals( coin)){
                tmp.add(key);
            }
        }
        Const.orgsTypes.put(OrgsInfoEnum.Bit_z, tmp);
    }

    private void  spiderDepth(String type) {
            String res = null;
            if(null != depth.get(makeName(type))) {
                //如果距离上次抓取不足1S 则不重复抓取数据
                long timeSub = System.currentTimeMillis() - depth.get(makeName(type)).getUpdateTime();
                if(timeSub < 1000) return;
            }

            try {
                res =HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/Market/depthSpot?symbol=" + makeName(type) , null,
                        MapBuilder.build().add("Connection", "keep-alive"));
                if("".equals(res) || null == res) return;
                JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                if(null == jsonNode) return;
                if(200 != jsonNode.get("status").asInt()) {
                    logger.error(jsonNode.toString());
                    removeInvalidCoin(deMakeName(type));
                    return;
                }

                jsonNode = jsonNode.get("data");
                if(null != jsonNode && jsonNode.isObject()) {
                    String name = makeName(type);
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
//
//                    logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                            name,
//                            depthVoMap.getBids().get(1).getPrice(),
//                            depthVoMap.getBids().get(1).getQty(),
//                            depthVoMap.getAsks().get(1).getPrice(),
//                            depthVoMap.getAsks().get(1).getQty());
                }
            } catch (Exception e) {
//                logger.warn("bitz depthSpot 数据抓取失败:{}", e.toString());
//                e.printStackTrace();
            }
    }


    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type);
        if(null == tickers.get(type)) return new Ticker();
        Ticker ticker = tickers.get(type).getLast();
        Long tmp = System.currentTimeMillis() - ticker.getUpdateTime();
        if(tmp > 10000) {
            return  new Ticker();
        }
        return ticker;
    }

    public void openSocket() {

    }

}
