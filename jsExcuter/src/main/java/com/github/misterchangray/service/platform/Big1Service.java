package com.github.misterchangray.service.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.init.Init;
import com.github.misterchangray.common.utils.*;
import com.github.misterchangray.controller.common.Const;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.vo.Depth;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.platform.vo.Ticker;
import com.github.misterchangray.service.po.AccountInfo;
import org.apache.http.HttpHost;
import org.primeframework.jwt.Signer;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class Big1Service extends BaseService {
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    ThreadPoolTaskExecutor executor;

    Logger logger = LoggerFactory.getLogger(Big1Service.class);

    /**
     * //保存bigone得所有交易币种；
     * 结构如下
     * [{
     * baseAsset:{uuid: "5451d95d-1477-475c-897b-8cd6a01dcb30", symbol: "DTA", name: "DATA"}
     * baseScale:0
     * name:"DTA-BTC"
     * quoteAsset:{uuid: "0df9c3c3-255a-46d7-ab82-dedae169fba9", symbol: "BTC", name: "Bitcoin"}
     * quoteScale:8
     * uuid:"04fffce5-51a1-4cb8-81e6-946ce5cb08c2"
     * }]
     */
    public static JsonNode markets;
    //保存每种币每秒钟的价格 只保存3600秒
    public static Map<String, ArrayDeque<Ticker>> tickers = MapBuilder.build();
    //保存每种币的交易深度
    public static Map<String, DepthVo> depth = MapBuilder.build();

    private static String baseUrl = OrgsInfoEnum.Big1.getOrgRestUrlSpot();

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

    public static String makeName(String type) {
        return type.replace("_", "-").toUpperCase();
    }
    public String deMakeName(String type) {
        return type.replace("-", "_").toLowerCase();
    }


    //移出没用的交易对
    public static void removeInvalidCoin(String coin) {
        List<String> s = Const.orgsTypes.get(OrgsInfoEnum.Big1);
        List<String> tmp = new ArrayList();
        for(String key : s) {
            if(!key .equals( coin)){
                tmp.add(key);
            }
        }
        Const.orgsTypes.put(OrgsInfoEnum.Big1, tmp);
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

        Long tmp = System.currentTimeMillis();
    }

    @Override
    public Boolean init() {
//        initMarkets();
//        openSocket();
        return true;
    }


    private void initMarkets() {
        String url = OrgsInfoEnum.Big1.getOrgRestUrlSpot() + "/markets";
        try {
            String res = HttpUtilManager.getInstance().requestHttpGet(url, null, null);
            if(null == res || "".equals(res)) return;
            markets = JSONUtils.buildJsonNode(res).get("data");
        } catch (Exception e) {
//            e.printStackTrace();
            logger.warn("bigone markets 数据抓取失败:{}", e.toString());
        }
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {

        return null;
    }



    private String getMarketId(String type) {
        if(null == markets) {
            this.initMarkets();
            return null;
        }
        for(JsonNode tmp : markets) {
            if(tmp.get("name").asText().equals(makeName(type))) {
                return tmp.get("uuid").asText();
            }
        }
        return null;
    }
    @Override
    public ResultSet<String> buy(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        String url = OrgsInfoEnum.Big1.getOrgRestUrlSpot() + "/viewer/orders";
        Signer signer = HMACSigner.newSHA256Signer(sKey);
        JWT jwt = new JWT();
        jwt.claims.put("type", "OpenAPI");
        jwt.claims.put("sub", aKey);
        jwt.claims.put("nonce", System.currentTimeMillis() * 1000000);
        String encodedJWT = JWT.getEncoder().encode(jwt, signer);

        try {
            String marketId = getMarketId(type.toString());
            if(null == marketId) {
                logger.warn("bigone 下单失败,没有该交易对:{}", type);
                return null;
            }
            String  res = HttpUtilManager.getInstance().requestHttpPost(url, null,
                    MapBuilder.build().add("market_id", marketId).add("side","BID")
                    .add("price", String.valueOf(price)).add("amount", String.valueOf(amount)),
                    MapBuilder.build().add("Connection", "keep-alive").add("Authorization", "Bearer " + encodedJWT)
            );
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result &&  null != result.get("data") && null != result.get("data").get("id")) {
                return ResultSet.build(ResultEnum.SUCCESS).setData(result.get("data").get("id").asText()) ;
            } else {
                // "errors":[{"path":["add_order"],"message":"Insufficient balance","locations":[{"line":2,"column":0}],"code":40006}],"data":null}
                if(res.contains("Insufficient balance")) {
                    //处理余额不足
                    return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
                }
                return ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }

    @Override
    public ResultSet<String> sell(CoinSpot type, BigDecimal price, BigDecimal amount, String aKey, String sKey) {
        String url = OrgsInfoEnum.Big1.getOrgRestUrlSpot() + "/viewer/orders";
        Signer signer = HMACSigner.newSHA256Signer(sKey);
        JWT jwt = new JWT();
        jwt.claims.put("type", "OpenAPI");
        jwt.claims.put("sub", aKey);
        jwt.claims.put("nonce", System.currentTimeMillis() * 1000000);
        String encodedJWT = JWT.getEncoder().encode(jwt, signer);

        try {
            String marketId = getMarketId(type.toString());
            if(null == marketId) {
                logger.warn("bigone 下单失败,没有该交易对:{}", type);
                return null;
            }
            String  res = HttpUtilManager.getInstance().requestHttpPost(url, null,
                    MapBuilder.build().add("market_id", marketId).add("side","ASK")
                            .add("price", String.valueOf(price)).add("amount", String.valueOf(amount)),
                    MapBuilder.build().add("Connection", "keep-alive").add("Authorization", "Bearer " + encodedJWT)
            );
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result && null != result.get("data") && null != result.get("data").get("id")) {
                return ResultSet.build(ResultEnum.SUCCESS).setData( result.get("data").get("id").asText());
            } else {
                // "errors":[{"path":["add_order"],"message":"Insufficient balance","locations":[{"line":2,"column":0}],"code":40006}],"data":null}
                if(res.contains("Insufficient balance")) {
                    //处理余额不足
                    return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
                }
                return ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
            logger.warn("bigone sell 下单失败:{}", e.toString());
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }



    @Override
    public ResultSet<String> cancelOrder(String orderId, CoinSpot symbol, String aKey, String sKey) {
        if (null == orderId) return null;

        String url = String.format("/viewer/orders/%s/cancel", orderId);
        Signer signer = HMACSigner.newSHA256Signer(sKey);
        JWT jwt = new JWT();
        jwt.claims.put("type", "OpenAPI");
        jwt.claims.put("sub", aKey);
        jwt.claims.put("nonce", System.currentTimeMillis() * 1000000);
        String encodedJWT = JWT.getEncoder().encode(jwt, signer);

        try {
            String  res = HttpUtilManager.getInstance().requestHttpPost( OrgsInfoEnum.Big1.getOrgRestUrlSpot() + url,null,null,
                MapBuilder.build().add("Connection", "keep-alive").add("Authorization", "Bearer " + encodedJWT)
            );
            JsonNode result = JSONUtils.buildJsonNode(res);
            if(null != result && null != result.get("data") && null != result.get("data").get("id")) {
                return ResultSet.build(ResultEnum.SUCCESS).setData( result.get("data").get("id").asText());
            } else {
                logger.error(res);
                return ResultSet.build(ResultEnum.FAILURE).setMsg(res);
            }
        } catch (Exception e) {
            logger.warn("bigone cancel 撤单失败:{}", e.toString());
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
    }


    private void spiderAccountData(long execTime) {
        List<Object> objects = mongoDbService.findAll(ApiConfigs.collectionName, ApiConfigs.class);
        for(Object o : objects) {
            if (o instanceof ApiConfigs) {
                ApiConfigs config = (ApiConfigs) o;
                if(config.getOrg() != OrgsInfoEnum.Big1) continue;

                String res = null;
                Signer signer = HMACSigner.newSHA256Signer(config.getsKey());
                JWT jwt = new JWT();
                jwt.claims.put("type", "OpenAPI");
                jwt.claims.put("sub", config.getaKey());
                jwt.claims.put("nonce", System.currentTimeMillis() * 1000000);
                String encodedJWT = JWT.getEncoder().encode(jwt, signer);
                AccountInfo accountInfo = new AccountInfo();
                try {
                    res = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/viewer/accounts", null,
                            MapBuilder.build().add("Connection", "keep-alive").add("Authorization", "Bearer " + encodedJWT)
                    );
                    //交易深度
                    accountInfo.setRawData(res);
                    //{"data":[{"locked_balance":"0","balance":"0","asset_uuid":"04479958-d7bb-40e4-b153-48bd63f2f77f","asset_id":"NKC"},{"locked_balance":"0","balance":"0","asset_uuid":"04c8da0e-44fd-4d71-aeb0-8f4d54a4a907","asset_id":"UBTC"}]}
                    JsonNode jsonNode = JSONUtils.buildJsonNode(res);
                    if(null != res && null != jsonNode && jsonNode.get("data").isArray()) {
                        for(JsonNode tmp : jsonNode.get("data")) {
                            if(0 < tmp.get("balance").asDouble() || 0 < tmp.get("locked_balance").asDouble()) {
                                accountInfo.getBalanceInfo().put(tmp.get("asset_id").asText(), MapBuilder.build()
                                        .add(AccountInfo.balance, tmp.get("balance").asDouble())
                                        .add(AccountInfo.locked_balance,  tmp.get("locked_balance").asDouble())
                                );
                            }
                        }

                        accountInfo.setOrgName(config.getOrg().getOrgName());
                        accountInfo.setAccount(config.getAccount());
                        accountInfo.setaKey(config.getaKey());
                        accountInfo.setType(AccountInfo.spot);
                        accountInfo.setTime(execTime);


                        if(accountInfo.getBalanceInfo().keySet().size() == 0) return;
                        Criteria criteria = Criteria.where("aKey").is(config.getaKey()).and("type").is(AccountInfo.spot);
                        mongoDbService.update(criteria, MapBuilder.build().add("last", false), AccountInfo.collectionName, AccountInfo.class);
                        mongoDbService.insert(accountInfo,  AccountInfo.collectionName);
                    }
                } catch (Exception e) {
                    logger.warn("bigone account 数据抓取失败:{}", e.toString());
                    spiderAccountData(execTime);
//            e.printStackTrace();
                }
            }
        }

    }

    //获取ticker信息
    private void spiderTicker() {
        String res = null;

        //如果距离上次抓取不足1S 则不重复抓取数据
       for(String key : tickers.keySet()) {
           long timeSub = System.currentTimeMillis() - tickers.get(key).getLast().getUpdateTime();
           if(timeSub < 1000) return;
       }
        try {
            res = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/tickers", null, MapBuilder.build().add("Connection", "keep-alive"));
            //交易深度
            if("".equals(res) || null == res) return;
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if(null == jsonNode) return;
            if(null != jsonNode.get("errors")) {
                logger.error(jsonNode.get("errors").toString());
                return;
            }
            if (null != jsonNode.get("data") && jsonNode.get("data").isArray()) {
                jsonNode.get("data").forEach(item -> {
                    for (String type : Const.orgsTypes.get(OrgsInfoEnum.Big1)) {
                        String name = makeName(type);
                        if (item.get("market_id").asText().equals(name)) {
                            ArrayDeque<Ticker> secPrice = tickers.get(name);
                            if (null == secPrice) {
                                secPrice = new ArrayDeque(Const.maxCacheTypes);
                            }

                            if (Const.maxCacheTypes < secPrice.size()) {
                                secPrice.removeFirst();
                            }
                            secPrice.add(new Ticker(item.get("close").asText(), item.get("high").asText(), item.get("low").asText(),
                                    item.get("bid").get("price").asText(),item.get("ask").get("price").asText(), item.get("volume").asText()));
                            tickers.put(name, secPrice);
                        }
                    }

                });
            }
        } catch (Exception e) {
//            logger.warn("bigone ticker 数据抓取失败:{}", e.toString());
//            e.printStackTrace();
        }
    }


    //big1没有socket接口
    public void openSocket() { }

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

    /**
     * 获取交易深度
     *
     * @param type 交易对
     */
    private void spiderDepth(String type) {
        String res = null;
        try {
            Long startTime = System.currentTimeMillis();
            if(null != depth.get(makeName(type))) {
                //如果距离上次抓取不足1S 则不重复抓取数据
                long timeSub = System.currentTimeMillis() - depth.get(makeName(type)).getUpdateTime();
                if(timeSub < 1000) return;
            }


            res = HttpUtilManager.getInstance().requestHttpGet(baseUrl + "/markets/" + type + "/depth", null, MapBuilder.build().add("Connection", "keep-alive"));
            if("".equals(res) || null == res) return;
            JsonNode jsonNode = JSONUtils.buildJsonNode(res);
            if(null == jsonNode) return;
            DepthVo depthVoMap = depth.get(type);
            if (null == depthVoMap) depthVoMap = new DepthVo();

            if(null != jsonNode.get("errors")) {
                removeInvalidCoin(deMakeName(type));
                logger.error(jsonNode.get("errors").toString());
                return;
            }
            if (jsonNode.isObject()) {
                ListBuilder cacheTmp = ListBuilder.build();
                for (JsonNode tmp : jsonNode.get("data").get("bids")) {
                    cacheTmp.append(new Depth(tmp.get("price").asDouble(), tmp.get("amount").asDouble()));
                }
                depthVoMap.setBids(cacheTmp);
                cacheTmp = ListBuilder.build();
                for (JsonNode tmp : jsonNode.get("data").get("asks")) {
                    cacheTmp.append(new Depth(tmp.get("price").asDouble(), tmp.get("amount").asDouble()));
                }
                depthVoMap.setAsks(cacheTmp);
                depthVoMap.setUpdateTime(startTime); //平均抓取时间再500mm
                depth.put(makeName(type), depthVoMap);
//                System.out.println("抓取时间 - " + (System.currentTimeMillis() - startTime));
//                logger.info("{}, 买一价:{}, 个数:{}; 卖一价:{}, 个数:{}",
//                        type,
//                        depthVoMap.getBids().get(1).getPrice(),
//                        depthVoMap.getBids().get(1).getQty(),
//                        depthVoMap.getAsks().get(1).getPrice(),
//                        depthVoMap.getAsks().get(1).getQty());
            }
        } catch (Exception e) {
            logger.warn("bigone depthSpot 数据抓取失败:{}", e.toString());
//            e.printStackTrace();
        }
    }
}
