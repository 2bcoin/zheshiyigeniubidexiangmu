package com.github.misterchangray.service.platform;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.libs.binance.api.client.BinanceApiClientFactory;
import com.github.misterchangray.libs.binance.api.client.BinanceApiRestClient;
import com.github.misterchangray.libs.binance.api.client.domain.OrderSide;
import com.github.misterchangray.libs.binance.api.client.domain.OrderType;
import com.github.misterchangray.libs.binance.api.client.domain.TimeInForce;
import com.github.misterchangray.libs.binance.api.client.domain.account.Account;
import com.github.misterchangray.libs.binance.api.client.domain.account.AssetBalance;
import com.github.misterchangray.libs.binance.api.client.domain.account.NewOrder;
import com.github.misterchangray.libs.binance.api.client.domain.account.NewOrderResponse;
import com.github.misterchangray.libs.binance.api.client.domain.market.Candlestick;
import com.github.misterchangray.libs.binance.api.client.domain.market.CandlestickInterval;
import com.github.misterchangray.libs.binance.api.client.exception.BinanceApiException;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.*;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.github.misterchangray.service.po.AccountBalance;
import com.github.misterchangray.service.po.AccountInfo;
import com.neovisionaries.ws.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BiAnService extends BaseService {
    Logger logger = LoggerFactory.getLogger(BiAnService.class);
    @Autowired
    private MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    //保存每种币每秒钟的价格 只保存3600条数据
    public static Map<String, ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String, DepthVo> depth = MapBuilder.build();


    @Override
    public List<Kline> getRecordsSpot(CoinSpot symbol, String cycle, Long size) {
        try {
            BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance();
            BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();

            //处理symbol
            String sy = symbol.toString().replace("_", "").toUpperCase();

            List<Candlestick> list = binanceApiRestClient.getCandlestickBars(
                    sy, klineCycleMap.get(cycle), size.intValue(), null, null);
            List<Kline> klines = new ArrayList<>();
            for (Candlestick candlestick : list) {
                Long openTime = candlestick.getOpenTime();
                Double open = new BigDecimal(candlestick.getOpen()).doubleValue();
                Double close = new BigDecimal(candlestick.getClose()).doubleValue();
                Double low = new BigDecimal(candlestick.getLow()).doubleValue();
                Double high = new BigDecimal(candlestick.getHigh()).doubleValue();
                Double volume = new BigDecimal(candlestick.getVolume()).doubleValue();
                Kline kline = new Kline(openTime, open, close, low, high, volume);
                kline.setInfo(JSONUtils.buildJsonNode(JSONUtils.obj2json(candlestick)));
                klines.add(kline);
            }
            return klines;
        }catch (BinanceApiException e){
            if (e.getError().getMsg().contains("Invalid symbol")){
                System.out.println("币种无效");
            }
        }
        return null;
    }

    private static HashMap<String, CandlestickInterval> klineCycleMap = new HashMap<String, CandlestickInterval>() {{
        //1min, 5min, 15min, 30min, 1hour, 2hour, 4hour, 6hour, 12hour, day, 3day, week
        this.put("1min", CandlestickInterval.ONE_MINUTE);
        this.put("5min", CandlestickInterval.FIVE_MINUTES);
        this.put("15min", CandlestickInterval.FIFTEEN_MINUTES);
        this.put("30min", CandlestickInterval.HALF_HOURLY);
        this.put("1hour", CandlestickInterval.HOURLY);
        this.put("2hour", CandlestickInterval.TWO_HOURLY);
        this.put("4hour", CandlestickInterval.FOUR_HOURLY);
        this.put("6hour", CandlestickInterval.SIX_HOURLY);
        this.put("12hour", CandlestickInterval.TWELVE_HOURLY);
        this.put("day", CandlestickInterval.DAILY);
        this.put("3day", CandlestickInterval.THREE_DAILY);
        this.put("week", CandlestickInterval.WEEKLY);
    }};

    @Override
    public ResultSet<String> buy(CoinSpot symbol, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        //TODO 未测试
        BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();

        //处理symbol
        String sy = symbol.toString().replace("_", "").toUpperCase();

        NewOrder newOrder = new NewOrder(sy, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC,amount.toString());
        NewOrderResponse newOrderResponse = binanceApiRestClient.newOrder(newOrder);
        System.out.println(newOrderResponse);
        String res = JSONUtils.obj2json(newOrderResponse);
        System.out.println(res);

        return null;
    }

    @Override
    public ResultSet<String> sell(CoinSpot symbol, BigDecimal price, BigDecimal amount, String aKey, String sKey) {

        //TODO 未测试
        BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();

        //处理symbol
        String sy = symbol.toString().replace("_", "").toUpperCase();

        NewOrder newOrder = new NewOrder(sy, OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC,amount.toString());
        NewOrderResponse newOrderResponse = binanceApiRestClient.newOrder(newOrder);
        System.out.println(newOrderResponse);
        String res = JSONUtils.obj2json(newOrderResponse);
        System.out.println(res);

        return null;
    }

    @Override
    public ResultSet<String> cancelOrder(String orderId, CoinSpot symbol, String aKey, String sKey) {
        return null;
    }

    @Override
    public Order getOrderSpot(String orderId, CoinSpot symbol, String aKey, String sKey) {
        return null;
    }

    @Override
    public List<Order> getOrdersSpot(CoinSpot symbol, String aKey, String sKey) {
        return null;
    }



    @Override
    public String customRequest(String method, String url, Map<String, String> params, String aKey, String sKey) {
        return null;
    }


    public static String makeName(String type) {
        return type.replace("_", "");
    }

    @Override
    public DepthVo getDepthSpot(String type) {
        if (null != type) type = makeName(type);

        DepthVo depthVo = depth.get(type);
        if (null == depthVo) return null;
        Long tmp = System.currentTimeMillis() - depthVo.getUpdateTime();
        return depthVo;
    }

    public static void main(String[] a) {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对websocket启用代理
        ProxySettings settings = Init.webSocketFactory.getProxySettings();
        settings.setHost(proxyHost);
        settings.setPort(Integer.parseInt(proxyPort));
        //对HTTP启用代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        // 对SOCKS开启代理
        System.setProperty("socks.proxyHost", proxyHost);
        System.setProperty("socks.proxyPort", proxyPort);

//        openSocket("/ws/!ticker@arr");
//        for(String type : Const.orgsTypes.get(OrgsInfoEnum.OKEX)) {
//            String command = String.format("/%s@depth20", makeName(type));
//            openSocket(command);
//        }



    }

    private void parseDepth(JsonNode res, String command) {
        String type = command.replace("@depth20", "");
        type = type.replace("/", "");
        DepthVo depthVoMap = depth.get(makeName(type));
        if (null == depthVoMap) depthVoMap = new DepthVo();

        if (res.isObject()) {
            ListBuilder cacheTmp = ListBuilder.build();
            for (JsonNode tmp : res.get("bids")) {
                cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
            }
            ;
            depthVoMap.setBids(cacheTmp);
            cacheTmp = ListBuilder.build();
            for (JsonNode tmp : res.get("asks")) {
                cacheTmp.append(new Depth(tmp.get(0).asDouble(), tmp.get(1).asDouble()));
            }
            ;
            depthVoMap.setAsks(cacheTmp);
            depthVoMap.setUpdateTime(System.currentTimeMillis());
            depth.put(makeName(type), depthVoMap);

//                        logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                        type,
//                        depthVoMap.getBids().get(1).getPrice(),
//                        depthVoMap.getBids().get(1).getQty(),
//                        depthVoMap.getAsks().get(1).getPrice(),
//                        depthVoMap.getAsks().get(1).getQty());
        }
    }

    private void parseTicker(JsonNode res) {
        if (res.isArray()) {
            for (JsonNode tmp : res) {
                String name = tmp.get("s").asText();
                ArrayDeque<Ticker> secPrice = tickers.get(name);
                if (null == secPrice) {
                    secPrice = new ArrayDeque(Const.maxCacheTypes);
                }

                if (Const.maxCacheTypes < secPrice.size()) {
                    secPrice.removeFirst();
                }
                secPrice.add(new Ticker(
                        tmp.get("a").asText(), tmp.get("l").asText(), tmp.get("h").asText(),
                        null, null, null
                ));
                tickers.put(makeName(name), secPrice);
            }
        }
    }

    public void openSocket(String command) {
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.BiAn.getOrgSocketUrlSpot() + command);
            ws.setPingInterval(3 * 1000);
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(BiAnService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- 币安 websocket连接成功[{}] 详情[{}] ---------------------------", DateUtils.now(null), command);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- 币安 websocket 连接被断开,尝试重新连接[{}] 详情[{}]---------------------------", DateUtils.now(null), command);
                    //连接断开后清空所有数据
                    tickers = MapBuilder.build();
                    depth = MapBuilder.build();
                    openSocket(command);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    if (null == text && "".equals(text)) return;
                    JsonNode res = JSONUtils.buildJsonNode(text);
                    if (null != res.get("bids") && res.get("bids").isArray()) {
                        parseDepth(res, command);
                    }
                    if (null != res && res.isArray() && res.get(0).get("e").asText().contains("Ticker")) {
                        parseTicker(res);
                    }
                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- 币安 websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocket(command);
        }
    }

    @Override
    public Boolean init() {
//        openSocket("/ws/!ticker@arr");
//        for (String type : Const.orgsTypes.get(OrgsInfoEnum.OKEX)) {
//            String command = String.format("/%s@depth20", makeName(type));
//            openSocket(command);
//        }
        return null;
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {


        return null;
    }

    @Override
    public Double convertAsset(String symbol, Double amount, String to) {
        if(to.equalsIgnoreCase("btc")) {
            if(symbol.equalsIgnoreCase("btc")) return amount;
            double s = Double.parseDouble(this.getTickerSpot(symbol + "_btc").getLast());
            return s * amount;
        } else if(to.equalsIgnoreCase("_usdt")){
            if(symbol.equalsIgnoreCase("usdt")) return amount;
            double s = Double.parseDouble(this.getTickerSpot(symbol + "_usdt").getLast());
            return s * amount;
        }
        return 0d;
    }

    @Override
    public List<AccountBalance> getAccount(String aKey, String sKey) {
        try {
            BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance(aKey, sKey);
            BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();
            Account account = binanceApiClientFactory.newRestClient().getAccount();

            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setInfo(JSONUtils.buildJsonNode(JSONUtils.obj2json(account.getBalances())));
            accountBalance.setaKey(aKey);
            accountBalance.setType(AccountBalance.spot);
            accountBalance.setOrgName(OrgsInfoEnum.BiAn.getOrgName());
            accountBalance.setTime(System.currentTimeMillis());

            for (AssetBalance tmp : account.getBalances()) {
//                if(null != symbol && !symbol.equalsIgnoreCase(tmp.getAsset())) continue;
                if (Double.valueOf(tmp.getFree()) > 0 || Double.valueOf(tmp.getLocked()) > 0) {
                    Double total = Double.valueOf(tmp.getFree()) + Double.valueOf(tmp.getLocked());
                    accountBalance.setBalance_btc(Utils.convertBigDecimal(convertAsset("xbt", total, "btc")));
                    accountBalance.setBalance_usd(Utils.convertBigDecimal(convertAsset("xbt", total, "usdt")));
                }
            }
            return ListBuilder.build().append(accountBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void spiderAccountData(long execTime) {
        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for (Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if (config.getOrg() != OrgsInfoEnum.BiAn) continue;

                try {
                    BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance(config.getaKey(), config.getsKey());
                    BinanceApiRestClient binanceApiRestClient = binanceApiClientFactory.newRestClient();
                    Account account = binanceApiClientFactory.newRestClient().getAccount();
                    AccountInfo accountInfo2 = new AccountInfo();
                    accountInfo2.setRawData(JSONUtils.obj2json(account.getBalances()));

                    for (AssetBalance tmp : account.getBalances()) {
                        if (Double.valueOf(tmp.getFree()) > 0 || Double.valueOf(tmp.getLocked()) > 0) {
                            accountInfo2.getBalanceInfo().put(tmp.getAsset(), MapBuilder.build()
                                    .add(AccountInfo.balance, Double.valueOf(tmp.getFree()))
                                    .add(AccountInfo.locked_balance, Double.valueOf(tmp.getLocked()))
                            );
                        }
                    }
                    accountInfo2.setOrgName(config.getOrg().getOrgName());
                    accountInfo2.setAccount(config.getAccount());
                    accountInfo2.setaKey(config.getaKey());
                    accountInfo2.setType(AccountInfo.spot);
                    accountInfo2.setTime(execTime);

                    if (accountInfo2.getBalanceInfo().keySet().size() == 0) return;
                    Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.spot);
                    mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                    mongoDbService.insert(accountInfo2, AccountInfo.collectionName);
                } catch (Exception e) {
                    spiderAccountData(execTime);
                    logger.warn("bian account 数据抓取失败:{}", e.toString());
                }

            }
        }
    }


    @Override
    public Ticker getTickerSpot(String type) {
        type = makeName(type).toUpperCase();
        if (null == tickers.get(type)) return new Ticker();
        Ticker ticker = tickers.get(type).getLast();
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
