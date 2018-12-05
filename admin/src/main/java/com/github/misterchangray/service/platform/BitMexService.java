package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.libs.bitmex.client.BitmexRestClient;
import com.github.misterchangray.libs.bitmex.entity.BitmexOrder;
import com.github.misterchangray.libs.bitmex.util.Util;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.dto.RunningImage;
import com.github.misterchangray.service.jsexcutor.dto.ScriptDto;
import com.github.misterchangray.service.jsexcutor.exception.FMZException;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.platform.vo.*;
import com.github.misterchangray.service.po.AccountBalance;
import com.neovisionaries.ws.client.*;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class BitMexService extends BaseService {
    Logger logger = LoggerFactory.getLogger(BiboxService.class);
    //保存每种币得深度信息 只保存3600条数据
    public static Map<String,DepthVo> depthOtc = MapBuilder.build();

    //保存最近一次设置得杠杆倍率
    private static Map<String, Integer> lastLeverage = new HashMap<>();


    @Override
    public Ticker getTickerOtc(CoinOtc coinOtc, String contract_type) throws Exception {
        String api = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + "/api/v1/instrument?symbol=" + contract_type;
        String res = HttpUtilManager.getInstance().requestHttpGet(api, null, null);
        JsonNode jsonNode = JSONUtils.buildJsonNode(res);
        if(null != jsonNode) {
            Ticker ticker = new Ticker();
            ticker.setInfo(jsonNode.get(0));
            ticker.setUpdateTime(DateUtils.utcToDate(jsonNode.get(0).get("openingTimestamp").asText()).getTime());
            ticker.setBuy(jsonNode.get(0).get("bidPrice").asText());
            ticker.setSell(jsonNode.get(0).get("askPrice").asText());
            ticker.setHigh(jsonNode.get(0).get("highPrice").asText());
            ticker.setLow(jsonNode.get(0).get("lowPrice").asText());
            ticker.setLast(jsonNode.get(0).get("lastPrice").asText());
            ticker.setVol(jsonNode.get(0).get("volume").asText());
            return ticker;
        }
        return null;
    }

    @Override
    public DepthVo getDepthOtc(CoinOtc coinOtc, String contract_type) {
        return depthOtc.get(contract_type);
    }

    @Override
    public List<Kline> getRecordsOtc(CoinOtc symbol, String contract_type, String cycle, Long size) throws Exception {
        String api = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + "/api/v1/trade/bucketed";
        Map<String, String > convertCycle = new HashMap<String, String>(){{
            //            1m,5m,1h,1d
            this.put("1min", "1m");
            this.put("5min", "5m");
            this.put("1hour", "1h");
            this.put("day", "1d");
        }};
        if(null == convertCycle.get(cycle)) throw  new RuntimeException("bitmex cycle 只支持 [1min, 5min, 1hour, day]");

        Map<String, String> params = new HashMap<>();
        params.put("binSize", convertCycle.get(cycle));
        params.put("symbol", contract_type);
        params.put("count", String.valueOf(size));
        params.put("reverse", "true");
        params.put("partial", "true");

        String res = HttpUtilManager.getInstance().requestHttpGet(api, params, null);
        JsonNode jsonNode = JSONUtils.buildJsonNode(res);
        if(null == jsonNode) return null;
        List<Kline> klines = null;
        if(jsonNode.isArray() && 0 < jsonNode.size()) {
            klines = new ArrayList<>();
            for(JsonNode jsonNode1 : jsonNode) {
                Kline kline = new Kline(
                        DateUtils.utcToDate(jsonNode1.get("timestamp").asText()).getTime(),
                        jsonNode1.get("open").asDouble(),
                        jsonNode1.get("close").asDouble(),
                        jsonNode1.get("low").asDouble(),
                        jsonNode1.get("high").asDouble(),
                        jsonNode1.get("volume").asDouble()
                        );
                kline.setInfo(jsonNode1);
                klines.add(kline);
            }
        }
        Collections.reverse(klines);
        return klines;

    }
    public static void main(String[] a) {
//        System.out.println(JSONUtils.obj2json(ListBuilder.build().append(new RunningImage("1", new Thread(),1l, new ScriptDto()))));
        //对apache httpclient 使用代理
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        //对apache httpclient 使用代理
        Init.httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));

        //对websocket启用代理
        ProxySettings settings =Init. webSocketFactory.getProxySettings();
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

        String aKey = "kPEL3JCX9N2XBYZZ2oCFsq9d";
        String sKey = "DwAMj7Bql9xoSIYmHG0xb8eiyvBMWoLOuktiIFfG6jx7REv8";
//        new BitMexService().setLeverage(1, "XBTUSD", "kPEL3JCX9N2XBYZZ2oCFsq9d",
//                "DwAMj7Bql9xoSIYmHG0xb8eiyvBMWoLOuktiIFfG6jx7REv8");
        String s = null;
//        s = new BitMexService().futureOrder(CoinOtc.btc, "XBTUSD", "duo", 1,
//        null, com.github.misterchangray.service.jsexcutor.util.Utils.convertBigDecimal(1), aKey, sKey).getData();
//        System.out.println(s);
//        s = new BitMexService().cancelOrderFuture("ad44572e-8104-4647-2dc1-dab54cad9e5e", CoinOtc.btc, "XBTUSD", aKey, sKey).getData();


        List a2 = null;
        try {
            a2 = new BitMexService().getOrdersOtc(CoinOtc.btc, "XBTUSD", aKey, sKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        new BitMexService().getAccount(aKey,sKey);
        System.out.println(a2);
    }

    /**
     * 设置杠杆倍率
     * @param lever_rate
     * @param symbol
     * @param aKey
     * @param sKey
     * @return
     */
    public boolean setLeverage(Integer lever_rate, String symbol, String aKey, String sKey) throws Exception {
        //检查杠杆倍率是否和上次设置得相同，如果相同则直接返回true
        if(null != lastLeverage.get(symbol) && lever_rate == lastLeverage.get(symbol)) return true;

        String api =  "/api/v1/position/leverage";
        String path = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + api;

        MapBuilder params =  MapBuilder.build().add("leverage", String.valueOf(lever_rate)).add("symbol", symbol);
        String expires = getExpires();
        String res = HttpUtilManager.getInstance().requestHttpPost(path, null, params,
                MapBuilder.build()
                    .add("API-expires", expires)
                    .add("API-key", aKey)
                    .add("Api-signature", SignUtil.bitmexSign("POST", api, expires, null, params, aKey, sKey)));
        JsonNode resNode = JSONUtils.buildJsonNode(res);
        if(null != resNode && null != resNode.get("leverage") &&
                resNode.get("leverage").asInt() == lever_rate) {
            lastLeverage.put(symbol, lever_rate);
            return true;
        }

        return false;
    }

    @Override
    public ResultSet<String> futureOrder(CoinOtc symbol, String contract_type, String direct, Integer lever_rate, BigDecimal price, BigDecimal amount, String aKey, String sKey) throws Exception{
        if(!setLeverage(lever_rate, contract_type, aKey, sKey)) return ResultSet.build(ResultEnum.FAILURE);
        if(null == direct)  return ResultSet.build(ResultEnum.FAILURE);

        Map<String, String> convertDirect = new HashMap<String, String>(){{
            this.put("duo", "Buy");
            this.put("kong", "Sell");
            this.put("pingduo", "Sell");
            this.put("pingkong", "Buy");
        }};

        BitmexRestClient client = new BitmexRestClient(true, aKey, sKey);
        BitmexOrder order = new BitmexOrder();
        order.setSymbol(contract_type);
        order.setSide(convertDirect.get(direct));
        order.setOrderQty(null == amount ? null : amount.doubleValue());
        order.setOrdType("Limit");
        if(direct.contains("ping")) {
            order.setExecInst("Close");
            order.setSide(null);
        }
        if(null == price) {
            order.setOrdType("Market");
            order.setPrice(null);
        } else {
            order.setPrice(price.doubleValue());
        }
        BitmexOrder result = client.submitOrder(order);
        if(null != result.getOrderID()) {
            return ResultSet.build(ResultEnum.SUCCESS).setData(result.getOrderID());
        }
        return ResultSet.build(ResultEnum.FAILURE).setMsg(JSONUtils.obj2json(result));
    }

    @Override
    public ResultSet<String> cancelOrderFuture(String orderId, CoinOtc symbol, String contractType, String aKey, String sKey) throws Exception{
        BitmexRestClient client = new BitmexRestClient(true, aKey, sKey);
        BitmexOrder bitmexCancelOrder = new BitmexOrder();
        bitmexCancelOrder.setOrderID(orderId);
        if(0 < client.cancelOrder(bitmexCancelOrder).length) {
            return ResultSet.build(ResultEnum.SUCCESS).setData(orderId);
        };
        return ResultSet.build(ResultEnum.FAILURE);
    }


    @Override
    public Order getOrderOtc(String orderId, CoinOtc symbol, String contractType, String aKey, String sKey)  throws Exception {
        return null;
    }


    @Override
    public List<Order> getOrdersOtc(CoinOtc symbol, String contractType, String aKey, String sKey)  throws Exception {
        String api =  "/api/v1/order";
        String path = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + api;

        MapBuilder params =  MapBuilder.build()
                .add("count", "50")
                .add("filter", "%7B%22open%22:true%7D")
                .add("reverse", "true")
                .add("symbol", String.valueOf(contractType));

        String expires = getExpires();
        String res = HttpUtilManager.getInstance().requestHttpGet(path, params,
                MapBuilder.build()
                        .add("API-expires", expires)
                        .add("API-key", aKey)
                        .add("Api-signature", SignUtil.bitmexSign("GET", api, expires, params, null, aKey, sKey)));
        JsonNode resNode = JSONUtils.buildJsonNode(res);
        if(null != resNode && resNode.isArray()) {
            ListBuilder listBuilder = ListBuilder.build();
            for(JsonNode tmp : resNode) {
                Order order = new Order();
                order.setInfo(tmp);
                order.setOrder_id(tmp.get("orderID").asText());
                order.setAmount(tmp.get("orderQty").asText());
                order.setPrice(tmp.get("price").asText());
                order.setSide(tmp.get("side").asText().toLowerCase());
                order.setContractType(tmp.get("symbol").asText());
                order.setSide(tmp.get("price").asText());
                order.setLeverRate(tmp.get("price").asText());
                listBuilder.append(order);
            }
            return  listBuilder;
        }

        return null;
    }

    private String getExpires() {
        return String.valueOf((System.currentTimeMillis()/1000) + 2000);
    }
    @Override
    public List<AccountBalance> getAccount(String aKey, String sKey) throws  Exception {
        String api =  "/api/v1/user/wallet";
        String path = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + api;

        MapBuilder params =  MapBuilder.build().add("currency", "XBt");
        String expires = getExpires();
        String res = HttpUtilManager.getInstance().requestHttpGet(path, params,
                MapBuilder.build()
                        .add("API-expires", expires)
                        .add("API-key", aKey)
                        .add("Api-signature", SignUtil.bitmexSign("GET", api, expires, params, null, aKey, sKey)));
        JsonNode resNode = JSONUtils.buildJsonNode(res);
        if(null != resNode && null != resNode.get("account")) {
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setInfo(resNode);
            accountBalance.setaKey(aKey);
            accountBalance.setType(AccountBalance.otc);
            accountBalance.setOrgName(OrgsInfoEnum.Bitmex.getOrgName());
            accountBalance.setTime(System.currentTimeMillis());
            double balance = (((resNode.get("amount").asLong() / 10000.0) *  0.0001)*100000)/100000;
            accountBalance.setBalance(Utils.convertBigDecimal(balance));
            accountBalance.setBalance_btc(Utils.convertBigDecimal(convertAsset("xbt", accountBalance.getBalance().doubleValue(), "btc")));
            accountBalance.setBalance_usd(Utils.convertBigDecimal(convertAsset("xbt", accountBalance.getBalance().doubleValue(), "usdt")));
            return ListBuilder.build().append(accountBalance);
        }

        return null;
    }

    @Override
    public List<Position> getPositionOtc(CoinOtc symbol, String contractType, String aKey, String sKey) throws Exception {
        String api =  "/api/v1/position/isolate";
        String path = OrgsInfoEnum.Bitmex.getOrgRestUrlOtc() + api;

        MapBuilder params =  MapBuilder.build().add("symbol", String.valueOf(contractType));
        String expires = getExpires();
        String res = HttpUtilManager.getInstance().requestHttpPost(path,null, params,
                MapBuilder.build()
                        .add("API-expires", expires)
                        .add("API-key", aKey)
                        .add("Api-signature", SignUtil.bitmexSign("POST", api, expires, null, params, aKey, sKey)));
        JsonNode resNode = JSONUtils.buildJsonNode(res);
        if(null != resNode) {
            ListBuilder listBuilder = ListBuilder.build();
            if(0 < resNode.get("currentQty").asInt()) {
                Position position = new Position();
                position.setInfo(resNode);
                position.setContractType(resNode.get("symbol").asText());
                position.setMarginLevel(Utils.convertBigDecimal(resNode.get("leverage").asInt()));
                position.setPrice(Utils.convertBigDecimal(resNode.get("avgEntryPrice").asDouble()));
                position.setType(0);
                position.setAmount(Utils.abs(Utils.convertBigDecimal(resNode.get("currentQty").asInt())));
                listBuilder.append(position);

            }
            if(0 > resNode.get("currentQty").asInt()) {
                Position position = new Position();
                position.setInfo(resNode);
                position.setContractType(resNode.get("symbol").asText());
                position.setMarginLevel(Utils.convertBigDecimal(resNode.get("leverage").asInt()));
                position.setPrice(Utils.convertBigDecimal(resNode.get("avgEntryPrice").asDouble()));
                position.setType(1);
                position.setAmount(Utils.abs(Utils.convertBigDecimal(resNode.get("currentQty").asInt())));
                listBuilder.append(position);
            }

            return listBuilder;
        }
        return null;
    }

    @Override
    public Double convertAsset(String symbol, Double amount, String to) throws Exception {
        if(to.equalsIgnoreCase("btc")) {
            if(symbol.equalsIgnoreCase("xbt")) return amount;
        } else {
            double s = Double.parseDouble(this.getTickerOtc(CoinOtc.btc, "XBT").getLast());
            return s * amount;
        }
        return 0d;
    }

    @Override
    public String customRequest(String method, String url, Map<String, String> params, String aKey, String sKey) throws Exception {
        String expires = getExpires();
        MapBuilder header = MapBuilder.build();
        String api = url.substring(OrgsInfoEnum.Bitmex.getOrgRestUrlOtc().length(), url.length());
        header.put("api-signature", SignUtil.bitmexSign(method, api, expires, params, null, aKey, sKey));
        header.put("api-expires", expires);
        header.put("api-key", aKey);
        String res = null;
        res = HttpUtilManager.getInstance().request(method, url, params, null, header);
        return res;
    }

    public static String makeName(String type) {
        return type.replace("_","").toUpperCase() ;
    }
    @Override
    public Boolean init() {
        openSocketOtc();
        return super.init();
    }
    public void openSocketOtc() {
        try {
            WebSocket ws = Init.webSocketFactory.createSocket(OrgsInfoEnum.Bitmex.getOrgSocketUrlOtc());
            ws.setPingInterval(3 * 1000);
            ws.setPingPayloadGenerator(new PayloadGenerator() {
                @Override
                public byte[] generate() {
                    return "ping".getBytes();
                }
            });
            ws.addListener(new WebSocketAdapter() {
                Logger logger = LoggerFactory.getLogger(OKEXService.class);

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    logger.info("--------------------------- bitmex websocket连接成功[{}] ---------------------------", DateUtils.now(null));
                    websocket.sendText("{\"op\": \"subscribe\", \"args\": [\"orderBook10\"]}");
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    logger.info("--------------------------- bitmex websocket连接连接被断开,尝试重新连接[{}] ---------------------------", DateUtils.now(null));
                    //连接断开后清空所有数据
                }


                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    JsonNode jsonNode = JSONUtils.buildJsonNode(text);
                    if(null != jsonNode.get("data") && "orderBook10".equalsIgnoreCase(jsonNode.get("table").asText())) {
                        jsonNode = jsonNode.get("data").get(0);
                        String name = jsonNode.get("symbol").asText();

                        DepthVo depthVoMap = depthOtc.get(name);
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

                        depthOtc.put(name, depthVoMap);


//                        System.out.println(depthOtc.get("XBTUSD").getAsks().get(2).getPrice() + ", " +
//                                depthOtc.get("XBTUSD").getAsks().get(2).getQty() +", " +
//                                depthOtc.get("XBTUSD").getBids().get(2).getPrice() +", " +
//                                depthOtc.get("XBTUSD").getBids().get(2).getQty());
                    }

                }
            });
            ws.connect();
        } catch (Exception e) {
            logger.warn("--------------------------- bitmex websocket连接连接出现错误,尝试重新连接[{}];详情{} ---------------------------", DateUtils.now(null), e.toString());
            openSocketOtc();
        }


    }



}
