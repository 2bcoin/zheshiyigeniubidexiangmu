package com.github.misterchangray.service.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.BigDecimalUtils;
import com.github.misterchangray.common.utils.HttpUtilManager;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.MapBuilder;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.*;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Kline;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.github.misterchangray.service.po.AccountInfo;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 对冲止损方案
 */
@Service
public class StopLossService extends BaseService {
    Logger logger = LoggerFactory.getLogger(StopLossService.class);
    public static boolean started = false; //是否启动
    public static Long startedTime = null; //启动时间

    private static java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();
    @Autowired
    OKEXService OKEXService;
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    BiAnService biAnService;
    @Autowired
    BitfinexService bitfinexService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    @Autowired
    HuoBiService huoBiService;
    @Autowired
    Big1Service big1Service;
    @Autowired
    OTCBTCService OTCBTCService;
    @Autowired
    FcoinService fcoinDataService;
    @Autowired
    ZbSiteService zbSiteService;
    @Autowired
    BitzService bitzService;
    @Autowired
    CoinbaseProService coinbaseProService;
    @Autowired
    DigiFinexService digiFinexService;
    @Autowired
    BiboxService biboxService;
    @Autowired
    KrakenService krakenService;

    private static MapBuilder param = MapBuilder.build().add("type", "2hour").add("contract_type", "quarter").add("size", "100").add("since", "true");
    private static SymbolStr[] symbols = {
            new SymbolStr("eth_usdt", 0.5, 10.0),
            new SymbolStr("btc_usdt", 0.5, 100.0)
    };
    private static String baseUrl = "https://www.okex.com";
    private static Core core = new Core();

    private static Map<String, List<Kline>> klines = new HashMap<>();
    private static Map<String, Ticker> tickers = new HashMap<>();
    private static Map<String, DepthVo> depths = new HashMap<>();

    private static Map<String, Map<String, Object>> trades = new HashMap<>(); //当前交易状态
    //对冲止损账号
    ApiConfigs apiConfigs = new ApiConfigs(OrgsInfoEnum.OKEX, "597febd1-7a45-4b4f-b6cd-986d7f1457c1","1558120C1938CFCA2A25299F40526D67");
    private int ganggan = 20; //杠杆大小

    public static void main(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
        StopLossService stopLossService = new StopLossService();
//        stopLossService.getFutureTicker("eth_usdt");
//        stopLossService.getFutureDepth("eth_usdt");
//        stopLossService.getFutureKLine("eth_usdt");


        System.out.println((int) BigDecimal.valueOf(123.176).doubleValue());
    }


    private void getFutureTicker(SymbolStr symbolStr) {
        try {
            String symbol = symbolStr.getSymbol().replace("usdt", "usd");
            //获取当前ticker信息
            String res1 = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/api/v1/future_ticker.do", param.add("symbol", symbol), null);
            if (null != res1) {
                JsonNode rest = JSONUtils.buildJsonNode(res1);
                rest = rest.get("ticker");
                tickers.put(symbolStr.getSymbol(), new Ticker(
                        rest.get("last").asText(), rest.get("high").asText(), rest.get("low").asText(),
                        rest.get("buy").asText(), rest.get("sell").asText(), rest.get("vol").asText()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFutureKLine(SymbolStr symbolStr) {
        try {
            String symbol = symbolStr.getSymbol().replace("usdt", "usd");
            //获取当前K线数据
            String res2 = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/api/v1/future_kline.do", param.add("symbol", symbol).add("size", "100"), null);
            if (null != res2) {
                JsonNode res3 = JSONUtils.buildJsonNode(res2);
                List<Kline> kline = new ArrayList<>(100);
                for (JsonNode item : res3) {
                    kline.add(new Kline(item.get(0).asLong(),
                            item.get(1).asDouble(), item.get(4).asDouble(),
                            item.get(3).asDouble(), item.get(2).asDouble(),
                            item.get(5).asDouble()));
                }
                klines.put(symbolStr.getSymbol(), kline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFutureDepth(SymbolStr symbolStr) {
        try {
            String symbol = symbolStr.getSymbol().replace("usdt", "usd");
            //获取深度信息
            String res3 = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/api/v1/future_depth.do", param.add("symbol", symbol).add("size", "20"), null);
            JsonNode rest = JSONUtils.buildJsonNode(res3);
            if (null != res3) {
                DepthVo depthVo = new DepthVo();
                List<Depth> asks = new ArrayList<>();
                List<Depth> bids = new ArrayList<>();

                for (JsonNode tmp : rest.get("asks")) {
                    asks.add(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                }
                for (JsonNode tmp : rest.get("bids")) {
                    bids.add(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
                }
                depthVo.setAsks(asks);
                depthVo.setBids(bids);
                depths.put(symbolStr.getSymbol(), depthVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTicker2(String type) {
        type = type.toLowerCase();
        return OKEXService.getTickerSpot(type).getLast();
    }

    private BigDecimal getTotailUsd() {
        Object os = mongoDbService.find(new Query(Criteria.where("time").is(HuoBiService.lastSpiderAccountData)), AccountInfo.collectionName, AccountInfo.class);
        List tmp = (List) os;
        BigDecimal total = new BigDecimal(0);
        for (Object o : tmp) {
            if (o instanceof AccountInfo) {
                AccountInfo accountInfo = (AccountInfo) o;

                Map<String, Object> ba = accountInfo.getBalanceInfo();
                if (0 != ba.keySet().size()) {
                    Iterator<String> iterator = ba.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        if (ba.get(key) instanceof Map) {
                            Map value = (Map) ba.get(key);

                            if (key.toLowerCase().equals("usdt") || key.toLowerCase().equals("usd")) {
                                if (null != value.get("balance")) {
                                    total = BigDecimalUtils.add(total, new BigDecimal((double) value.get("balance")));
                                }
                                if (null != value.get("locked_balance")) {
                                    total = BigDecimalUtils.add(total, new BigDecimal((double) value.get("locked_balance")));
                                }
                            } else {
                                String type = key + "_usdt";
                                String lastPrice = getTicker2(type);
                                BigDecimal tmp2 = new BigDecimal(0);
                                if (null != lastPrice) {
                                    if (null != value.get("balance")) {
                                        total = BigDecimalUtils.add(
                                                total,
                                                BigDecimalUtils.mul(BigDecimal.valueOf(Double.valueOf(lastPrice)), new BigDecimal((double) value.get("balance")))
                                        );
                                        ;
                                    }
                                    if (null != value.get("locked_balance")) {
                                        total = BigDecimalUtils.add(
                                                total,
                                                BigDecimalUtils.mul(BigDecimal.valueOf(Double.valueOf(lastPrice)), new BigDecimal((double) value.get("locked_balance")))
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }

    private int start = 0; //程序再第15个周期开始工作;也就是75s后才开始工作

    //扫描对冲条件
    public void scanerForOkex() {
        if(start < 15) {
            start ++;
            return;
        }
        depths.clear();
        tickers.clear();
        klines.clear();
        for (SymbolStr symbol : symbols) {
            getFutureTicker(symbol);
            getFutureKLine(symbol);
            getFutureDepth(symbol);
        }

        if (klines.size() == symbols.length && tickers.size() == symbols.length && depths.size() == symbols.length) {
            List<Double> ethMA5 = calcMACD(klines.get("eth_usdt"), 5);
            List<Double> ethMA10 = calcMACD(klines.get("eth_usdt"), 10);
            List<Double> ethMA30 = calcMACD(klines.get("eth_usdt"), 30);
            Ticker ethTicker = tickers.get("eth_usdt");

            List<Double> btcMA5 = calcMACD(klines.get("btc_usdt"), 5);
            List<Double> btcMA10 = calcMACD(klines.get("btc_usdt"), 10);
            List<Double> btcMA30 = calcMACD(klines.get("btc_usdt"), 30);
            Ticker btcTicker = tickers.get("btc_usdt");

            Map last = null;
            BigDecimal total = null;
            for (SymbolStr symbol : symbols) {
                if (null == trades.get(symbol.getSymbol())) {
                    if (Double.valueOf(ethTicker.getLast()) < Double.valueOf(ethMA30.get(ethMA30.size() - 1))
                            && Double.valueOf(btcTicker.getLast()) < Double.valueOf(btcMA30.get(btcMA30.size() - 1))
                            && ethMA5.get(ethMA5.size() - 1) < ethMA10.get(ethMA10.size() - 1)
                            && btcMA5.get(btcMA5.size() - 1) < btcMA10.get(btcMA10.size() - 1)) {
                        if(0 == BiAnService.tickers.size()) return;; //ticker数据没有准备好不进行开单操作
                        //开空单;
                        if(null == total) total = getTotailUsd();
                        Double price = depths.get(symbol.getSymbol()).getBids().get(2).getPrice();

                        Long amount = (long) BigDecimalUtils.div(
                                BigDecimalUtils.div(
                                        BigDecimalUtils.mul(total, BigDecimal.valueOf(symbol.getBili().doubleValue())),
                                        BigDecimal.valueOf(ganggan)
                                ),
                                BigDecimal.valueOf(symbol.getHs())
                        ).doubleValue();
                        logger.info("计算结果" + symbol + "下单个数" + amount);
                        amount = 1l;
                        OKEXService.futureOrder(CoinOtc.getCoin(symbol.getSymbol()), "quarter", "kong", ganggan,
                                BigDecimal.valueOf(price), BigDecimal.valueOf(amount),
                                apiConfigs.getaKey(), apiConfigs.getsKey());

                        trades.put(symbol.getSymbol(), MapBuilder.build()
                                .add("price", price)
                                .add("amount", amount)
                        );

                    }
                } else {
                    Map<String, Object> tmp = trades.get(symbol.getSymbol());
                    Double orderPrice = (Double) tmp.get("price");
                    Double lastPrice = depths.get(symbol.getSymbol()).getAsks().get(2).getPrice();
                    Long amount = (long) tmp.get("amount");

                    if ((Double.valueOf(ethTicker.getLast()) > Double.valueOf(ethMA30.get(ethMA30.size() - 1))
                            && Double.valueOf(btcTicker.getLast()) > Double.valueOf(btcMA30.get(btcMA30.size() - 1)))) {

                        OKEXService.futureOrder(CoinOtc.getCoin(symbol.getSymbol()), "quarter", "pingkong", ganggan,
                                BigDecimal.valueOf(lastPrice), BigDecimal.valueOf(amount), apiConfigs.getaKey(), apiConfigs.getsKey());
                        trades.put(symbol.getSymbol(), null);

                        logger.info("均线满足条件平仓" + symbol + "," + lastPrice);
                    } else {
                        double lv = ((orderPrice - lastPrice) / orderPrice) * ganggan;
                        if (lv < -0.3) { //亏损30%强制平仓
                            OKEXService.futureOrder(CoinOtc.getCoin(symbol.getSymbol()), "quarter", "pingkong", ganggan,
                                    BigDecimal.valueOf(lastPrice), BigDecimal.valueOf(amount), apiConfigs.getaKey(), apiConfigs.getsKey());
                            trades.put(symbol.getSymbol(), null);

                            logger.info("亏损到3ls" +
                                    "0%强行平仓" + symbol + "," + lastPrice);
                        }
                    }
                }
            }
        } else {
            scanerForOkex();
        }
    }


    /**
     * 计算移动平均线
     *
     * @param klines
     * @param unit
     * @return
     */
    public List<Double> calcMACD(List<Kline> klines, int unit) {
        if (null == klines) return null;
        double[] closePrice = new double[klines.size()];
        for (int i = 0; i < klines.size(); i++) {
            closePrice[i] = Double.valueOf(klines.get(i).getClose());
        }
        double[] out = new double[klines.size()];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        //格式化小数输出
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(3);

        RetCode retCode = core.movingAverage(0, closePrice.length - 1, closePrice, unit, MAType.Sma, begin, length, out);
        if (retCode == RetCode.Success) {
            List<Double> l = new ArrayList<>(klines.size());
            for (Double d : out) {
                if (null != d && 0 != d) {
                    l.add(Double.valueOf(numberFormat.format(d)));
                }
            }
            return l;
        }
        return null;
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long startTime) {
        if (quartzEnum == QuartzEnum.Second5) {
            if(StopLossService.started) {
                scanerForOkex();
            }

        }
        return null;
    }
}


class SymbolStr {
    private String symbol;
    private Double bili; //币种下单时所占得比例
    private Double hs; //美元与张数换算


    public SymbolStr(String symbol, Double bili, Double hs) {
        this.symbol = symbol;
        this.bili = bili;
        this.hs = hs;
    }

    public Double getHs() {
        return hs;
    }

    public void setHs(Double hs) {
        this.hs = hs;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getBili() {
        return bili;
    }

    public void setBili(Double bili) {
        this.bili = bili;
    }

    @Override
    public String toString() {
        return "SymbolStr{" +
                "symbol='" + symbol + '\'' +
                ", bili=" + bili +
                ", hs=" + hs +
                '}';
    }
}