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
import java.util.Map;


/**
 * Kraken
 */
@Service
public class KrakenService extends BaseService {
    Logger logger = LoggerFactory.getLogger(KrakenService.class);
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    private MongoDbService mongoDbService;
    //保存每种币每秒钟的价格 只保存3600条数据
    public static Map<String,ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depth = MapBuilder.build();


    public static String makeName(String type) {
        return  type.replace("_", "")
                .replace("usdt", "usd")
                .toUpperCase();
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
    public Boolean init() {
        return super.init();
    }


    //获取ticker信息
    private void spiderTicker(String type) {
        String res = null;
        type = makeName(type);
        try {
            res = HttpUtilManager.getInstance().requestHttpGet(OrgsInfoEnum.Kraken.getOrgRestUrlSpot() + "/0/public/Ticker?pair=" + type,
                    null,
                    MapBuilder.build().add("Connection", "keep-alive"));
            if("".equals(res) || null == res) return;
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if(null == jsonNode) return;
            if(null != jsonNode.get("error") && 0 < jsonNode.get("error").size()) return;

            String name = makeName(type);
            ArrayDeque<Ticker> secPrice = tickers.get(name);
            if (null == secPrice) {
                secPrice = new ArrayDeque(Const.maxCacheTypes);
            }

            if (Const.maxCacheTypes < secPrice.size()) {
                secPrice.removeFirst();
            }
            jsonNode = jsonNode.get("result");
            secPrice.add(new Ticker());
            tickers.put(name, secPrice);

        } catch (Exception e) {
//            logger.warn("bigone ticker 数据抓取失败:{}", e.toString());
//            e.printStackTrace();
        }
    }


    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {

        if(QuartzEnum.Second5 == quartzEnum) {
//            Long current = System.currentTimeMillis();
//            for (String type : Const.orgsTypes.get(OrgsInfoEnum.Big1)) {
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        spiderTicker(type);
//                    }
//                });
//            }


        }
        return null;
    }



    public static void main(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

         //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));

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


//    /**
//     *
//     * @param type 获取得币种
//     */
//    public void loadTicker(String type) {
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
//        BinanceApiRestClient client = factory.newRestClient();
//
//        TickerStatistics tickerStatistics = client.get24HrPriceStatistics(type);
//        //TickerStatistics[priceChange=0.00001800,priceChangePercent=0.147,weightedAvgPrice=0.01220929,prevClosePrice=0.01225500,lastPrice=0.01226300,bidPrice=0.01226500,askPrice=0.01227200,
//        // openPrice=0.01224500,highPrice=0.01232300,lowPrice=0.01205800,volume=129162.18000000,openTime=1531538626154,closeTime=1531625026154,firstId=14172466,lastId=14196235,count=23770]
//        //lines: K线图, 依次是: 时间(ms), 开盘价, 最高价, 最低价, 收盘价, 成交量
//
////        String data = String.format("[%d,%.8f,%.8f,%.8f,%.8f,%.8f]",
////                jsonNode.get("date").asLong(),
////                JSONUtils.getJsonPathVal(jsonNode, "ticker.buy", 0).asDouble(),
////                JSONUtils.getJsonPathVal(jsonNode, "ticker.high", 0).asDouble(),
////                JSONUtils.getJsonPathVal(jsonNode, "ticker.low", 0).asDouble(),
////                JSONUtils.getJsonPathVal(jsonNode, "ticker.low", 0).asDouble(),
////                JSONUtils.getJsonPathVal(tickerStatistics.getVolume(), "ticker.vol", 0).asDouble()
////        );
//        System.out.println(tickerStatistics);
//    }
}
