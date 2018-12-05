package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.HttpUtilManager;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import org.apache.http.HttpHost;
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
public class BiboxService extends BaseService {
    Logger logger = LoggerFactory.getLogger(BiboxService.class);
    @Autowired
    private MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    //保存每种币每3秒钟的价格 只保存3600条数据
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public static String makeName(String type) {
        return  type.toUpperCase();
    }

    public static void main(String []a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));

//        spiderMarket("btc_usdt");
    }

    //抓取市场信息
    private void spiderMarket(String type) {
        String url = OrgsInfoEnum.Bibox.getOrgRestUrlSpot() + "/v1/mdata?cmd=market&pair=" + makeName(type);
        try {
            String  res = HttpUtilManager.getInstance().requestHttpGet(url,null, null);
            if(null == res || "".equals(res)) return;
            JsonNode result = JSONUtils.buildJsonNode(res);
            String name = null;

            if(null != result &&  null != result.get("cmd") && null != result.get("result")) {
                JsonNode node = result.get("result");
                name = node.get("coin_symbol").asText() + "_" + node.get("currency_symbol").asText();
                name = makeName(name);
                ArrayDeque<Ticker> secPrice = tickers.get(name);
                if (null == secPrice) {
                    secPrice = new ArrayDeque(Const.maxCacheTypes);
                }
                if (Const.maxCacheTypes < secPrice.size()) {
                    secPrice.removeFirst();
                }
                secPrice.add(new Ticker(
                        node.get("last").asText(), node.get("high").asText(),
                        node.get("low").asText(), null, null,
                        node.get("amount").asText()));
                tickers.put(name, secPrice);
            } else if(null != result && null != result.get("error") && 3106 ==result.get("error").get("code").asInt()) {
                removeInvalidCoin(type);
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            logger.warn("bibox ticker 数据抓取失败:{}", e.toString());
        }
    }

    //移出没用的交易对
    public static void removeInvalidCoin(String coin) {
        List<String> s = Const.orgsTypes.get(OrgsInfoEnum.Bibox);
        List<String> tmp = new ArrayList();
        for(String key : s) {
            if(!key .equals( coin)){
                tmp.add(key);
            }
        }
        Const.orgsTypes.put(OrgsInfoEnum.Bibox, tmp);
    }
    //抓取市场信息
    private void spiderMarketAll() {
        String url = OrgsInfoEnum.Bibox.getOrgRestUrlSpot() + "/v1/mdata?cmd=marketAll";
        try {
            String  res = HttpUtilManager.getInstance().requestHttpGet(url,null, null);
            if(null == res || "".equals(res)) return;
            JsonNode result = JSONUtils.buildJsonNode(res);
            String name = null;

            if(null != result &&  null != result.get("cmd") && result.get("result").isArray()) {
                for(JsonNode node : result.get("result")) {
                    name = node.get("coin_symbol").asText() + "_" + node.get("currency_symbol").asText();
                    name = makeName(name);
                    ArrayDeque<Ticker> secPrice = tickers.get(name);
                    if (null == secPrice) {
                        secPrice = new ArrayDeque(Const.maxCacheTypes);
                    }
                    if (Const.maxCacheTypes < secPrice.size()) {
                        secPrice.removeFirst();
                    }
                    secPrice.add(new Ticker(
                            node.get("last").asText(), node.get("high").asText(),
                            node.get("low").asText(), null, null,
                            node.get("amount").asText()));
                    tickers.put(name, secPrice);
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            logger.warn("bibox ticker 数据抓取失败:{}", e.toString());
        }
    }

    @Override
    public Boolean init() {
        return super.init();
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if(QuartzEnum.Second5 == quartzEnum) {
            for (String type : Const.orgsTypes.get(OrgsInfoEnum.Bibox)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        spiderMarket(type);
                    }
                });
            }

        }

        return null;
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



}
