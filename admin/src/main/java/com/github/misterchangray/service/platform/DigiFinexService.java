package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.CryptoUtils;
import com.github.misterchangray.common.utils.HttpUtilManager;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
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

import java.util.*;

@Service
public class DigiFinexService extends BaseService {
    Logger logger = LoggerFactory.getLogger(DigiFinexService.class);
    @Autowired
    private MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    //保存每种币每3秒钟的价格 只保存3600条数据
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();

    public static String makeName(String type) {
        return  type.replace("_", "").toUpperCase();
    }

    public static void main(String []a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));

        //此账号用于爬取数据
        ApiConfigs apiConfigs = new ApiConfigs(OrgsInfoEnum.DigiFinex, "5b87a279adaf9", "91ab633ac8f8f157a983e3c1ba21fa4805b87a279");

        spiderMarketAll(apiConfigs.getaKey(),apiConfigs.getsKey());
    }


    //参数签名
    private static String sign(TreeMap<String, String> param) {
        StringBuilder s = new StringBuilder();
        for(String key : param.keySet()) {
            s.append(param.get(key));
        }
        return CryptoUtils.encodeMD5(s.toString());
    }

    //抓取市场信息
    private static void spiderMarketAll(String aKey, String sKey) {
        String url = OrgsInfoEnum.DigiFinex.getOrgRestUrlSpot() + "/v2/ticker";
        try {
            TreeMap<String ,String > param = new TreeMap<>();
            param.put("apiKey", aKey);
            param.put("apiSecret",  sKey);
            param.put("symbol", "");
            param.put("timestamp", String.valueOf( System.currentTimeMillis()/1000));
            param.put("sign", sign(param));

            String  res = HttpUtilManager.getInstance().requestHttpGet(url,param, null);
            JsonNode result = JSONUtils.buildJsonNode(res);
            String name = null;

            if(null != result &&  null != result.get("code") && result.get("ticker").isObject()) {
                result = result.get("ticker");
                Iterator<Map.Entry<String, JsonNode>> t = result.fields();

               while (t.hasNext()) {
                   Map.Entry<String, JsonNode> tmp = t.next();

                   name = tmp.getKey();
                   name = name.split("_")[1] + "_" + name.split("_")[0];
                   name = makeName(name);
                   ArrayDeque<Ticker> secPrice = tickers.get(name);
                   if (null == secPrice) {
                       secPrice = new ArrayDeque(Const.maxCacheTypes);
                   }
                   if (Const.maxCacheTypes < secPrice.size()) {
                       secPrice.removeFirst();
                   }
                   secPrice.add(new Ticker(tmp.getValue().get("last").asText(), tmp.getValue().get("high").asText(), tmp.getValue().get("low").asText(),
                           tmp.getValue().get("buy").asText(), tmp.getValue().get("sell").asText(),tmp.getValue().get("vol").asText()));
                   tickers.put(name, secPrice);
               }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            logger.warn("DigiFinex ticker 数据抓取失败:{}", e.toString());
        }
    }

    @Override
    public Boolean init() {
        return super.init();
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        //此账号用于爬取数据
        ApiConfigs apiConfigs = new ApiConfigs(OrgsInfoEnum.DigiFinex, "5b87a279adaf9", "91ab633ac8f8f157a983e3c1ba21fa4805b87a279");

        if(QuartzEnum.Second5 == quartzEnum) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    spiderMarketAll(apiConfigs.getaKey(),
                            apiConfigs.getsKey());
                }
            });
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
