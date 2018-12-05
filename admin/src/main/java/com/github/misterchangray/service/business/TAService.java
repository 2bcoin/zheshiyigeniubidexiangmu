package com.github.misterchangray.service.business;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.DBEnum;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.BigDecimalUtils;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.controller.SocketController;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.controller.common.vo.ApiConfigs;
import com.github.misterchangray.controller.common.vo.SocketSession;
import com.github.misterchangray.controller.common.vo.TAConfig;
import com.github.misterchangray.controller.common.vo.TAConfigs;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.DbLogService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.*;
import com.github.misterchangray.service.platform.vo.DepthVo;
import com.github.misterchangray.service.po.DbLog;
import com.github.misterchangray.service.po.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 三角套利
 */
@Service
public class TAService extends BaseService {
    Logger logger = LoggerFactory.getLogger(TAService.class);
    @Autowired
    DbLogService dbLogService;
    @Autowired
    OKEXService OKEXService;
    @Autowired
    BiAnService biAnService;
    @Autowired
    MongoDbService mongoDbService;
    @Autowired
    HuoBiService huoBiService;
    @Autowired
    Big1Service big1Service;
    @Autowired
    OTCBTCService OTCBTCService;
    @Autowired
    FcoinService fcoinService;
    @Autowired
    BitfinexService bitfinexService;
    @Autowired
    ZbSiteService zbSiteService;
    @Autowired
    BitzService bitzService;
    @Autowired
    OKEXService okexService;
    @Autowired
    TradeService tradeService;

    @Autowired
    ThreadPoolTaskExecutor executor;
    private static  java.text.NumberFormat numberFormat = java.text.NumberFormat.getInstance();


    public static boolean isRunning = false; //三角当前状态; true运行中;false停止
    private static int buyCount = 0; //取卖几价
    public static Long startTime = null; //三角启动时间;
    public static List<TAConfigs> taConfig = new ArrayList<TAConfigs>();//三角交易配置
    public static double default_fv = 0.005; //三角交易默认利率阈值;大于此利率才会下单
    public static double default_fv_for_log = 0.002; //大于此利率将会被存入数据库
    //测试交易
    public void testTrade() {
//        ResultSet<String> res =   tradeService.buy(fcoinService.makeName("btc_usdt"),  BigDecimal.valueOf(6200), BigDecimal.valueOf(0.002), OrgsInfoEnum.Fcoin);
//        ResultSet<String> res2 =  tradeService.sell( fcoinService.makeName("btc_usdt"), BigDecimal.valueOf(7300), BigDecimal.valueOf(0.002), OrgsInfoEnum.Fcoin);
//        res = tradeService.cancel(res.getData(), fcoinService.makeName("btc_usdt"), OrgsInfoEnum.Fcoin);
//        res2 = tradeService.cancel(res2.getData(), fcoinService.makeName("btc_usdt"), OrgsInfoEnum.Fcoin);

//        ResultSet<String> res =  tradeService.buy( huoBiService.makeName("btc_usdt"),  BigDecimal.valueOf(6200), BigDecimal.valueOf(0.002), OrgsInfoEnum.HuoBi);
//        ResultSet<String> res2 = tradeService.sell(huoBiService.makeName("btc_usdt"),  BigDecimal.valueOf(7300), BigDecimal.valueOf(0.002), OrgsInfoEnum.HuoBi);
//        res = tradeService.cancel(res.getData(), huoBiService.makeName("btc_usdt"), OrgsInfoEnum.HuoBi);
//        res2 = tradeService.cancel(res2.getData(), huoBiService.makeName("btc_usdt"), OrgsInfoEnum.HuoBi);


//        ResultSet<String> res = tradeService.buy( okexService.makeName("btc_usdt"),  BigDecimal.valueOf(6200), BigDecimal.valueOf(0.002), OrgsInfoEnum.OKEX);
//        ResultSet<String> res2 = tradeService.sell( okexService.makeName("btc_usdt"), BigDecimal.valueOf(7300), BigDecimal.valueOf(0.002), OrgsInfoEnum.OKEX);
//        res = tradeService.cancel( res.getData(), okexService.makeName("btc_usdt"), OrgsInfoEnum.OKEX);
//        res2 = tradeService.cancel( res2.getData(),okexService.makeName("btc_usdt"), OrgsInfoEnum.OKEX);


//        ResultSet<String> res  = tradeService.buy( big1Service.makeName("btc_usdt"), BigDecimal.valueOf(6200), BigDecimal.valueOf(0.002), OrgsInfoEnum.Big1);
//        ResultSet<String> res2  =   tradeService.sell(  big1Service.makeName("btc_usdt"), BigDecimal.valueOf(7300), BigDecimal.valueOf(0.002), OrgsInfoEnum.Big1);
//        res = tradeService.cancel( res.getData(), big1Service.makeName("btc_usdt"),OrgsInfoEnum.Big1  );
//        res2 = tradeService.cancel( res2.getData(),big1Service.makeName("btc_usdt"), OrgsInfoEnum.Big1  );

//        ResultSet<String> res = tradeService.buy("btc_usdt", BigDecimal.valueOf(Double.valueOf(6200)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.Bitfinex);
//        ResultSet<String> res2 = tradeService.sell("btc_usdt", BigDecimal.valueOf(Double.valueOf(7300)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.Bitfinex);
//        res = tradeService.cancel(res.getData(), "", OrgsInfoEnum.Bitfinex);
//        res2 = tradeService.cancel(res2.getData(), "", OrgsInfoEnum.Bitfinex);


//        ResultSet<String> res2 = tradeService.sell("btc_usdt", BigDecimal.valueOf(Double.valueOf(7800)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.BiAn);
//        res2 = tradeService.cancel(res2.getData(), "btc_usdt", OrgsInfoEnum.BiAn);
//        ResultSet<String> res = tradeService.buy("btc_usdt", BigDecimal.valueOf(Double.valueOf(6200)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.BiAn);
//        res = tradeService.cancel(res.getData(), "btc_usdt", OrgsInfoEnum.BiAn);

//        try {
//            ResultSet<String> res2 = tradeService.sell("btc_usdt", BigDecimal.valueOf(Double.valueOf(7800)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.BiAn);
//            res2 = tradeService.cancel(res2.getData(), "btc_usdt", OrgsInfoEnum.BiAn);
//            ResultSet<String> res = tradeService.buy("btc_usdt", BigDecimal.valueOf(Double.valueOf(6200)), BigDecimal.valueOf(Double.valueOf(0.002)), OrgsInfoEnum.BiAn);
//            res = tradeService.cancel(res.getData(), "btc_usdt", OrgsInfoEnum.BiAn);
//        } catch (Exception e ) {
//            e.printStackTrace();
//        }



    }

    //跑三角的账号配置
    Map<OrgsInfoEnum, ApiConfigs> apiConfig = new HashMap<OrgsInfoEnum, ApiConfigs>(){{
        this.put(OrgsInfoEnum.Fcoin, new ApiConfigs(OrgsInfoEnum.Fcoin,  "cb4aa679d2d145969b79f7bfe21757dd", "c001610384f8433eb260799f0fc52f40"));
        this.put(OrgsInfoEnum.HuoBi, new ApiConfigs(OrgsInfoEnum.HuoBi,  "0f452066-7d78e572-1571d4c2-5883b", "f34950ac-59ac5cd2-2c3bdc03-976ea"));
        this.put(OrgsInfoEnum.Big1, new ApiConfigs(OrgsInfoEnum.Big1,  "c7254bb3-fa16-4c40-ba37-3b39f76275cf", "5FA2B380F019A1CD4B748F92711B02D91ECC75529C0D39B665CC464DD390A50A"));
        this.put(OrgsInfoEnum.OKEX, new ApiConfigs(OrgsInfoEnum.OKEX,"1c98269e-d8fd-4f89-a75a-97607fa1dde0", "1C40D16D25DEDD2AC55C55C9B1C9D5E4"));
        this.put(OrgsInfoEnum.Bitfinex, new ApiConfigs(OrgsInfoEnum.Bitfinex, "xG6ukxPEysokvYIQ4caki1TQcy8Q8ScuGB77ouoim3N", "2QOH3ZWpNzpZysha05Krv0PAN8s6e6toulFXEoP1S3k"));
        this.put(OrgsInfoEnum.BiAn, new ApiConfigs(OrgsInfoEnum.BiAn, "BPTh9qWoh4Z2LEetACLnlxfIAIS2rrMubuqaCz7BgMqcrwVClVjS1ZmswEB2oPDm","MnJ2VmezyxOf0gIY6qU6CKmeDEgE6HfeyijRi7SePVCWXWL9uqgEGYzdZMbVj0ts"));
    }};


    private int parseInt(Object t) {
        return Integer.parseInt(t.toString());
    }


    //从数据库中读取三角配置
    public void loadConfigs() {
        List<Object>  taConfigs = mongoDbService.findAll(TAConfigs.collectionName, TAConfigs.class);
        if(taConfigs.size() > 0) {
            taConfig.clear();
            for(Object t : taConfigs) {
                if(t instanceof TAConfigs) {
                    taConfig.add((TAConfigs) t);
                } else {
                    logger.error("三角配置错误;不能正确加载三角配置");
                }
            }
            dbLogService.info(DBEnum.SANJIAO, "成功从数据库中加载{}个三角规则", taConfig.size());
        }
    }

    /**
     * 获取所有配置得交易对
     * @return
     */
    public ResultSet getAllTypes() {
        return ResultSet.build().setData(TAService.taConfig);
    }

    /**
     * 获取最近一次启动到当前时间得日志
     * type all 推送全部数据; last 推送最新数据
     * @return
     */
    public void pushLog(SocketSession socketSession, DBEnum dbEnum) {
//        if(false == isRunning) return ;
        if(null == socketSession.getLastPushTime()) socketSession.setLastPushTime(startTime);

        ResultSet res = ResultSet.build();
        Query q = new Query();
        q.addCriteria(
                Criteria.where("time").gte(socketSession.getLastPushTime()).andOperator(Criteria.where("type").is(dbEnum.getDesc()))
        );

        Object data = mongoDbService.find(q, DbLog.collectionName, DbLog.class);

        try {
            if(socketSession.getSession().isOpen()) {
                socketSession.getSession().getBasicRemote().sendText(JSONUtils.obj2json(ResultSet.build().setData(data).setMsg(dbEnum.getDesc())));
                socketSession.setLastPushTime(System.currentTimeMillis());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //格式化bigdecimal
    private BigDecimal formatDecimal(BigDecimal b, Integer p) {
        numberFormat.setMaximumFractionDigits(36);
        if(null != p) {
            numberFormat.setMaximumFractionDigits(p);
        }
        //格式化小数输出
        numberFormat.setRoundingMode(RoundingMode.DOWN);
        numberFormat.setGroupingUsed(false);
        return BigDecimal.valueOf(Double.valueOf(numberFormat.format(b)));
    }


    /**
     * 1.eth_usdt
     * 2.btc_usdt
     * 3.eth_btc
     * 以在平台3 买入 eth 为正向
     */
    private void zhengxiang(double lvfz, TAConfig org1, TAConfig org2, TAConfig org3, DepthVo depth1, DepthVo depth2, DepthVo depth3) {
        BigDecimal org1BuyPrice = BigDecimal.valueOf(depth1.getBids().get(buyCount).getPrice());
        BigDecimal org1BuyCount = BigDecimal.valueOf(depth1.getBids().get(buyCount).getQty());
        BigDecimal org2SellPrice = BigDecimal.valueOf(depth2.getAsks().get(buyCount).getPrice());
        BigDecimal org2SellCount = BigDecimal.valueOf(depth2.getAsks().get(buyCount).getQty());
        BigDecimal org3SellPrice = BigDecimal.valueOf(depth3.getAsks().get(buyCount).getPrice());
        BigDecimal org3SellCount = BigDecimal.valueOf(depth3.getAsks().get(buyCount).getQty());

        BigDecimal minCount = BigDecimal.valueOf(1);
        BigDecimal org3Tmp;
        BigDecimal org1Tmp;
        BigDecimal org2Tmp;
        BigDecimal lr, lv;

        //计算三个交易对能同时成交的下单数量； 统一换算为 btc 数量：然后取最小值
        //* 1.eth_usdt      sell
        //* 2.btc_usdt      buy
        //* 3.eth_btc       buy
        List<BigDecimal> min = new ArrayList<>();
        min.add(BigDecimalUtils.mul(org3SellPrice, org3SellCount));
        min.add(BigDecimalUtils.mul(org3SellPrice, org1BuyCount));
        min.add(org2SellCount);
        Collections.sort(min);
        minCount = min.get(0);     //三个币种 同时能交易的最小 btc 数量
        int precision = parseInt(org2.getAmountPrecision());
        minCount = formatDecimal(minCount, precision);

        if(org3.getCoin().toLowerCase().endsWith("_btc")) {
            if(minCount.doubleValue() > 0.04) minCount = BigDecimal.valueOf(0.04);  // todo 目前最多下单 btc 数量
        } else if(org3.getCoin().toLowerCase().endsWith("_eth")) {
            if(minCount.doubleValue() > 1) minCount = BigDecimal.valueOf(1);  // todo 目前最多下单 eth 数量
        }

        BigDecimal count = BigDecimalUtils.div(minCount, org3SellPrice);
        precision = parseInt(org3.getAmountPrecision());
        if(parseInt(org1.getAmountPrecision()) < precision) precision = parseInt(org1.getAmountPrecision());
        count = formatDecimal(count, precision);

        if(org3.getMinAmount() <= count.doubleValue() && org1.getMinAmount() <= count.doubleValue() &&
                org2.getMinAmount() <= minCount.doubleValue()) {

            //正三角 交叉汇率 大于 实际汇率 应该买入
            // 1.在平台3 以 org3SellPrice 的价格买入 count 个交易对 总共花费 org3Tmp     eth_btc
            org3Tmp = BigDecimalUtils.mul(org3SellPrice, count);
            // 2.在平台1 以 org1BuyPrice 的价格卖出 org3Tmp 个交易对 总共赚取 org1Tmp     eth_usdt
            org1Tmp = BigDecimalUtils.mul(org1BuyPrice, count);
            //3.在平台2 以 org2SellPrice 的价格买入 org3Tmp 交易对 org2Tmp              btc_usdt
            org2Tmp = BigDecimalUtils.mul(org2SellPrice, minCount);

            //计算利润 ((org1Tmp / count) - count) + (count - org3Tmp)
            lr = BigDecimalUtils.add(BigDecimalUtils.sub(BigDecimalUtils.div(org1Tmp, org2SellPrice), minCount), BigDecimalUtils.sub(minCount, org3Tmp));
            //计算利率
            lv = BigDecimalUtils.div( lr, org3Tmp);
            //利率大于 default_fv_for_log 则打印日志
            if(lv.doubleValue() >= default_fv_for_log) {
                //操作前打印当前市场信息
                dbLogService.info(DBEnum.SANJIAOMONI,
                        "1.在{}买入{}(价格:{}, 数量:{});" +
                        "2.在{}卖出{}(价格:{}, 数量:{});" +
                        "3.在{}买入{}(价格:{}, 数量:{});" +
                        "本次交易盈利:{},{};利率:{};交易时市场信息:" +
                        "平台:{}, 买{}价,价格:{},数量:{};" +
                        "平台:{},卖{}价,价格:{},数量:{}; " +
                        "平台:{},卖{}价,价格:{},数量:{};",
                        org3.getOrg(), org3.getCoin(), org3SellPrice, count,
                        org1.getOrg(), org1.getCoin(), org1BuyPrice, count,
                        org2.getOrg(), org2.getCoin(), org2SellPrice, minCount,
                        formatDecimal(lr, null),  org3.getCoin().split("_")[1], lv,
                        org1.getOrg(),  buyCount, org1BuyPrice, org1BuyCount,
                        org2.getOrg(), buyCount, org2SellPrice, org2SellCount,
                        org3.getOrg(), buyCount, org3SellPrice, org3SellCount
                );
            }
            //利率大于 lvfz 时,开始操作
            if (lr.doubleValue() > 0 && lv.doubleValue() > lvfz) {
                List<Order> orders = new ArrayList<>();
                orders.add(new Order( org3.getOrg().getOrgName(), org3.getCoin(), Order.direc_buy, org3SellPrice, count, apiConfig.get(org3.getOrg()).getaKey(), apiConfig.get(org3.getOrg()).getsKey()));
                orders.add(new Order( org1.getOrg().getOrgName(), org1.getCoin(),Order.direc_sell, org1BuyPrice, count, apiConfig.get(org1.getOrg()).getaKey(), apiConfig.get(org1.getOrg()).getsKey()));
                orders.add(new Order( org2.getOrg().getOrgName(), org2.getCoin(),Order.direc_buy, org2SellPrice, minCount, apiConfig.get(org2.getOrg()).getaKey(), apiConfig.get(org2.getOrg()).getsKey()));
                List<Order> list = tradeService.executeOrder(orders);
                for(Order order : list) {
                    if(false == order.getResultSet().isSuccess()) {
                        TAService.isRunning = false;//如果有下单失败情况;暂停三角交易循环
                        dbLogService.info(DBEnum.SANJIAO, "三角交易失败;交易详情:{}", JSONUtils.obj2json(orders));
                        return;
                    }
                }
                //如没有任何错误则三角交易成功
                dbLogService.info(DBEnum.SANJIAO, "三角交易成功;交易详情:{}", JSONUtils.obj2json(orders));

            }
        }
    }

    /**
     * 1.eth_usdt
     * 2.btc_usdt
     * 3.eth_btc
     * 以在平台3 卖出 eth 为正向
     */
    private void  nixiang(double lvfz, TAConfig org1, TAConfig org2, TAConfig org3, DepthVo depth1, DepthVo depth2, DepthVo depth3) {
        BigDecimal org1SellPrice = BigDecimal.valueOf(depth1.getAsks().get(buyCount).getPrice());
        BigDecimal org1SellCount = BigDecimal.valueOf(depth1.getAsks().get(buyCount).getQty());
        BigDecimal org2BuyPrice = BigDecimal.valueOf(depth2.getBids().get(buyCount).getPrice());
        BigDecimal org2BuyCount = BigDecimal.valueOf(depth2.getBids().get(buyCount).getQty());
        BigDecimal org3BuyPrice = BigDecimal.valueOf(depth3.getBids().get(buyCount).getPrice());
        BigDecimal org3BuyCount = BigDecimal.valueOf(depth3.getBids().get(buyCount).getQty());

        BigDecimal count = BigDecimal.valueOf(1);
        BigDecimal org3Tmp;
        BigDecimal org1Tmp;
        BigDecimal org2Tmp;
        BigDecimal lr, lv;
        List<BigDecimal> min = new ArrayList<>();

        //计算三个交易对能同时成交的下单数量； 统一换算为 eth 数量：然后取最小值
        //* 1.eth_usdt      buy
        //* 2.btc_usdt      buy
        //* 3.eth_btc       sell
        min = new ArrayList<>();
        min.add(org3BuyCount);
        min.add(BigDecimalUtils.div(org2BuyCount , org3BuyPrice));
        min.add(org1SellCount);
        Collections.sort(min);
        count = min.get(0);

        if(org3.getCoin().toLowerCase().startsWith("btc_")) {
            if(count.doubleValue() > 0.04) count = BigDecimal.valueOf(0.04);  // todo 目前最多下单 btc 数量
        } else if(org3.getCoin().toLowerCase().startsWith("eth_")) {
            if(count.doubleValue() > 1) count = BigDecimal.valueOf(1); // todo 限制;每次最多只能搬1ETH
        }

        int precision = parseInt(org3.getAmountPrecision());
        if(parseInt(org1.getAmountPrecision()) < precision) precision = parseInt(org1.getAmountPrecision());
        count = formatDecimal(count, precision);

        //逆三角  应该卖出; 以卖出 eth_usdt 的方向 为 逆向
        // 1.在平台3 以org3BuyPrice 的价格卖出 count 个交易对 总共赚取 org3Tmp        eth_btc
        org3Tmp = BigDecimalUtils.mul(org3BuyPrice, count);
        // 2.在平台2 以org2BuyPrice 卖出 org3Tmp 个交易对,总共赚取 org2Tmp            btc_usdt
        org3Tmp = formatDecimal(org3Tmp, org2.getAmountPrecision());
        org2Tmp = BigDecimalUtils.mul(org2BuyPrice, org3Tmp);
        //3.在平台1 以 org1SellPrice 的价格 买入 count 个交易对 总共花费 org1Tmp      eth_usdt
        org1Tmp = BigDecimalUtils.mul(org1SellPrice, count);

        if(org3.getMinAmount() <= count.doubleValue() && org2.getMinAmount() <= org3Tmp.doubleValue()
                && org1.getMinAmount() <= count.doubleValue()) {
            //利润计算公式  (org2Tmp - org1Tmp) + ((org3Tmp - org2Tmp) * org2BuyPrice)
            lr = BigDecimalUtils.add(BigDecimalUtils.sub(org2Tmp, org1Tmp), BigDecimalUtils.mul(BigDecimalUtils.sub(BigDecimalUtils.mul(org3BuyPrice, count), org3Tmp), org2BuyPrice));
            lv = BigDecimalUtils.div(BigDecimalUtils.div(lr, org1SellPrice), count);

            //利率大于 default_fv_for_log 则打印日志
            if (lv.doubleValue() > default_fv_for_log) {
                dbLogService.info(DBEnum.SANJIAOMONI,
                        "1.在{}卖出{}(价格:{}, 数量:{});" +
                                "2.在{}卖出{}(价格:{}, 数量:{});" +
                                "3.在{}买入{}(价格:{}, 数量:{});" +
                                "本次交易盈利:{},{};利率:{};交易时市场信息:" +
                                "平台:{}, 卖{}价,价格:{},数量:{}; " +
                                "平台:{}, 买{}价,价格:{},数量:{};" +
                                "平台:{}, 买{}价,价格:{},数量:{};",
                        org3.getOrg(), org3.getCoin(), org3BuyPrice, count,
                        org2.getOrg(), org2.getCoin(), org2BuyPrice, org3Tmp,
                        org1.getOrg(), org1.getCoin(), org1SellPrice, count,
                        formatDecimal(lr, null), org2.getCoin().split("_")[1], lv,
                        org1.getOrg(), buyCount, org1SellPrice, org1SellCount,
                        org2.getOrg(), buyCount, org2BuyPrice, org2BuyCount,
                        org3.getOrg(), buyCount, org3BuyPrice, org3BuyCount
                );
            }
            //利率大于 lvfz 时,开始下单操作
            if (lr.doubleValue() > 0 && lv.doubleValue() > lvfz) {
                List<Order> orders = new ArrayList<>();
                orders.add(new Order(org3.getOrg().getOrgName(), org3.getCoin(), Order.direc_sell, org3BuyPrice, count ,apiConfig.get(org3.getOrg()).getaKey(), apiConfig.get(org3.getOrg()).getsKey()));
                orders.add(new Order(org2.getOrg().getOrgName(), org2.getCoin(), Order.direc_sell, org2BuyPrice, org3Tmp, apiConfig.get(org2.getOrg()).getaKey(), apiConfig.get(org2.getOrg()).getsKey()));
                orders.add(new Order(org1.getOrg().getOrgName(), org1.getCoin(), Order.direc_buy, org1SellPrice, count, apiConfig.get(org1.getOrg()).getaKey(), apiConfig.get(org1.getOrg()).getsKey()));
                List<Order> list = tradeService.executeOrder(orders);
                for (Order order : list) {
                    if (false == order.getResultSet().isSuccess()){
                        TAService.isRunning = false;//如果有三角下单失败的情况;停止三角交易循环

                        dbLogService.info(DBEnum.SANJIAO, "三角交易失败;交易详情:{}", JSONUtils.obj2json(orders));
                        return;
                    }
                }
                //如没有任何错误则三角交易成功
                dbLogService.info(DBEnum.SANJIAO, "三角交易成功;交易详情:{}", JSONUtils.obj2json(orders));

            }
        }
    }

    //根据深度扫描套利机会
    public void scaner(TAConfigs tmp) {
        TAConfig org1 = tmp.getTaConfig(0);
        TAConfig org2 = tmp.getTaConfig(1);
        TAConfig org3 = tmp.getTaConfig(2);
        double lvfz =  default_fv;
        if(null != tmp.getLv()) lvfz = tmp.getLv(); //利率阈值;大于此利率才会被成交

        if(org3.getOrg().getOrgName().equals("Big1") || org2.getOrg().getOrgName().equals("Big1") || org1.getOrg().getOrgName().equals("Big1")) {
                //提前手动抓取深度数据
                big1Service.triggerQuartz(null);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        DepthVo depth1 = depth(org1.getCoin(), org1.getOrg());
        DepthVo depth2 = depth(org2.getCoin(), org2.getOrg());
        DepthVo depth3 = depth(org3.getCoin(), org3.getOrg());
        if (null == depth1 || null == depth2 || null == depth3) {
            //没有数据直接返回
//                logger.error("获取不到某些交易对得深度信息:{}:{},{}:{},{}:{}", org1, depth1 == null ? "null" : "有数据",org2, depth2 == null ? "null" : "有数据", org3, depth3 == null ? "null" : "有数据");
            return;
        }

        BigDecimal org1SellPrice = BigDecimal.valueOf(depth1.getAsks().get(buyCount).getPrice());
        BigDecimal org2SellPrice = BigDecimal.valueOf(depth2.getAsks().get(buyCount).getPrice());
        BigDecimal org3SellPrice = BigDecimal.valueOf(depth3.getAsks().get(buyCount).getPrice());

        BigDecimal jxhl = BigDecimalUtils.div(org1SellPrice, org2SellPrice);//计算交叉汇率
        if (0 != BigDecimalUtils.sub(jxhl, org3SellPrice).doubleValue()) {
            //交叉汇率不等于实际汇率 则存在套利机会
            zhengxiang(lvfz, org1, org2, org3, depth1, depth2 , depth3);
            nixiang(lvfz, org1, org2, org3, depth1, depth2 , depth3);
        }
    }


    private DepthVo depth(String type, OrgsInfoEnum org) {
       DepthVo res = null;
        switch (org) {
            case BiAn:
                res = biAnService.getDepthSpot(type);
                break;
            case Big1:
                res = big1Service.getDepthSpot(type);
                break;
            case Bitfinex:
                res = bitfinexService.getDepthSpot(type);
                break;
            case Bit_z:
                res = bitzService.getDepthSpot(type);
                break;
            case Fcoin:
                res = fcoinService.getDepthSpot(type);
                break;
            case HuoBi:
                res = huoBiService.getDepthSpot(type);
                break;
            case OKEX:
                res = OKEXService.getDepthSpot(type);
                break;
            case OTCBTC:
                res = OTCBTCService.getDepthSpot(type);
                break;
            case Zb_site:
                res = zbSiteService.getDepthSpot(type);
                break;
        }
        ;
        return res;
    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if(QuartzEnum.Second3 == quartzEnum) {
            if(TAService.isRunning) {
                if(0 == TAService.taConfig.size()) {
                    loadConfigs();
                }
                for (TAConfigs tmp : TAService.taConfig) {
                    if(3 != tmp.size()) continue;
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                              scaner(tmp);
                        }
                    });
                }
            }
        }
        if(QuartzEnum.Second5 == quartzEnum) {
            //每5S推送下最新日志信息
            SocketController.clear();

            //推送三角交易数据
            CopyOnWriteArrayList<SocketSession> socketSessions = SocketController.subscribeSession.get(DBEnum.SANJIAO.getDesc());
            if(null != socketSessions && socketSessions.size() > 0) {
                for(SocketSession socketSession : socketSessions) {
                    if(null != socketSession && null != socketSession.getSession() &&
                            true == socketSession.getSession().isOpen()) {
                        pushLog(socketSession, DBEnum.SANJIAO);
                    }
                }

            };

            //推送三角模拟数据
            socketSessions = SocketController.subscribeSession.get(DBEnum.SANJIAOMONI.getDesc());
            if(null != socketSessions && socketSessions.size() > 0) {
                for(SocketSession socketSession : socketSessions) {
                    if(null != socketSession && null != socketSession.getSession() &&
                            true == socketSession.getSession().isOpen()) {
                        pushLog(socketSession, DBEnum.SANJIAOMONI);
                    }
                }

            };
        }
        return true;
    }
}
