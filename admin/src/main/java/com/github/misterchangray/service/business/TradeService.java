package com.github.misterchangray.service.business;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.DBEnum;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.common.DbLogService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.common.vo.order.BianOrderService;
import com.github.misterchangray.service.common.vo.order.FcoinOrderService;
import com.github.misterchangray.service.common.vo.order.HuobiOrderService;
import com.github.misterchangray.service.common.vo.order.OkexOrderService;
import com.github.misterchangray.service.platform.*;
import com.github.misterchangray.service.po.Order;
import com.mongodb.BasicDBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提供交易相关的接口
 * - 下买单
 * - 下买单
 * - 取消订单
 */
@Service
public class TradeService {
    Logger logger = LoggerFactory.getLogger(TradeService.class);
    @Autowired
    DbLogService dbLogService;
    @Autowired
    private MongoDbService mongoDbService;
    @Autowired
    private BianOrderService bianOrderService;
    @Autowired
    private  FcoinOrderService fcoinOrderService;
    @Autowired
    private  HuobiOrderService huobiOrderService;
    @Autowired
    private OkexOrderService okexOrderService;


    @Autowired
    private OKEXService okexService;
    @Autowired
    private BiAnService biAnService;
    @Autowired
    private HuoBiService huoBiService;
    @Autowired
    private Big1Service big1Service;
    @Autowired
    private OTCBTCService otcbtcService;
    @Autowired
    private FcoinService fcoinService;
    @Autowired
    private BitfinexService bitfinexService;


    //判断交易是否成功
    public boolean isSuccess(String id) {
        if(null == id || "-1".equalsIgnoreCase(id) || "false".equalsIgnoreCase(id)) return false;
        return true;
    }

    /**
     * 批量执行订单操作
     * 订单执行;
     * 对传入的订单进行下单操作;下单成功后将更新OrderId字段
     */
    public List<Order> executeOrder(List<Order> orders) {
        if(null == orders || 0 == orders.size()) return null;
        //有盈利,开始操作
        try {
            for(Order order : orders) {
                order.setDate(DateUtils.now(null));
                ResultSet<String> res =  tradeAdaptor(order.getOrg_direc(), order.getOrg_type(), null,
                        order.getOrg_price(), order.getOrg_count(), OrgsInfoEnum.getOrgsInfoEnumByName(order.getOrg_name()),  order.getOrg_aKey(),  order.getOrg_sKey());
                order.setResultSet(res);
                if(res.isSuccess()) {
                    order.setOrder_id(res.getData());
                } else {
                    dbLogService.info(DBEnum.ORDER,"平台 {} 下单失败;进入撤销订单步骤;订单详情:{}", order.getOrg_name(), JSONUtils.obj2json(orders));
                    throw new RuntimeException(order.getOrg_name() + ",平台下单失败;撤销所有订单");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boolean flag = true;
            for(Order order : orders) {
                if(false == isSuccess(order.getOrder_id())){
                    flag = isSuccess(order.getOrder_id());
                    break;
                }
            }
            if(false == flag) {
                //存在失败订单;马上撤单
                for(Order order : orders) {
                    if(isSuccess(order.getOrder_id())){
                        try {
                            dbLogService.info(DBEnum.ORDER,"正在撤销订单; 当前订单详情:{}", JSONUtils.obj2json(order));
                            tradeAdaptor(Order.direc_cancel, order.getOrg_type(), order.getOrder_id(), null, null,
                                    OrgsInfoEnum.getOrgsInfoEnumByName(order.getOrg_name()),  order.getOrg_aKey(),  order.getOrg_sKey());
                        } catch (Exception e) {
                            dbLogService.info(DBEnum.ORDER,"撤销订单失败:当前订单详情:{}", JSONUtils.obj2json(order));
                        }
                    }
                }
            }


            dbLogService.info(DBEnum.ORDER,"批量下单成功，交易详情:{}", JSONUtils.obj2json(orders));
            //无论是否成功都要保存订单信息
            BasicDBObject doc = new BasicDBObject().append("data", orders);
            mongoDbService.insert(doc, Order.collectionName);
            return orders;
        }
    }

    /**
     *
     * @param direcion  /buy/sell/cancel
     * @Param type 交易对
     * @param orderId   如果取消则要传入此参数
     * @param price     买卖价格
     * @param amount      买卖数量
     * @param orgsInfoEnum
     * @return
     */
    private ResultSet<String> tradeAdaptor(String direcion, String type, String orderId, BigDecimal price, BigDecimal amount, OrgsInfoEnum orgsInfoEnum, String aKey, String sKey) {
        if(orgsInfoEnum == OrgsInfoEnum.BiAn) {
            if(Order.direc_buy.equals(direcion) || Order.direc_sell.equals(direcion)) {
                return bianOrderService.placeOrder(aKey, sKey, biAnService.makeName(type).toUpperCase(), direcion, price.doubleValue(), amount.doubleValue());
            }
            if(Order.direc_cancel.equals(direcion)) {
                return bianOrderService.cancelOrder(aKey, sKey,  biAnService.makeName(type).toUpperCase(), orderId);
            }
        } else if(orgsInfoEnum == OrgsInfoEnum.Fcoin) {
            if(Order.direc_buy.equals(direcion) || Order.direc_sell.equals(direcion)) {
                return fcoinOrderService.placeOrder(aKey, sKey, fcoinService.makeName(type), direcion, price.doubleValue(), amount.doubleValue());
            }
            if(Order.direc_cancel.equals(direcion)) {
                return fcoinOrderService.cancelOrder(aKey, sKey, fcoinService.makeName(type), orderId);
            }
        } else if(orgsInfoEnum == OrgsInfoEnum.HuoBi) {
            if(Order.direc_buy.equals(direcion) || Order.direc_sell.equals(direcion)) {
                return huobiOrderService.placeOrder(aKey, sKey, huoBiService.makeName( type), direcion, price.doubleValue(), amount.doubleValue());
            }
            if(Order.direc_cancel.equals(direcion)) {
                return huobiOrderService.cancelOrder(aKey, sKey, huoBiService.makeName( type), orderId);
            }
        } else if(orgsInfoEnum == OrgsInfoEnum.OKEX) {
            if(Order.direc_buy.equals(direcion) || Order.direc_sell.equals(direcion)) {
                return okexOrderService.placeOrder(aKey, sKey, okexService.makeName( type), direcion, price.doubleValue(), amount.doubleValue());
            }
            if(Order.direc_cancel.equals(direcion)) {
                return okexOrderService.cancelOrder(aKey, sKey, okexService.makeName(type), orderId);
            }
        } else if(orgsInfoEnum == OrgsInfoEnum.Big1) {
            if(Order.direc_buy.equals(direcion)) {
                return big1Service.buy(CoinSpot.getCoin(big1Service.makeName(type)), price, amount, aKey, sKey);
            }
            if(Order.direc_sell.equals(direcion)) {
                return big1Service.sell(CoinSpot.getCoin(big1Service.makeName(type)), price, amount, aKey, sKey);
            }
            if(Order.direc_cancel.equals(direcion)) {
                return big1Service.cancelOrder(orderId, CoinSpot.getCoin(type), aKey, sKey);
            }
        } else if(orgsInfoEnum == orgsInfoEnum.Bitfinex) {
            if(Order.direc_buy.equals(direcion)) {
                return bitfinexService.buy(CoinSpot.getCoin(bitfinexService.makeName(type)), price, amount, aKey, sKey);
            }
            if(Order.direc_sell.equals(direcion)) {
                return bitfinexService.sell(CoinSpot.getCoin(bitfinexService.makeName(type)), price, amount, aKey, sKey);
            }
            if(Order.direc_cancel.equals(direcion)) {
                return bitfinexService.cancelOrder(orderId, CoinSpot.getCoin(type), aKey, sKey);
            }
        } else {
            logger.info("[{}]交易所暂时不支持交易操作", orgsInfoEnum.getOrgName());
            return null;
        }
        return null;
    }

    public ResultSet<String> buy(String type, BigDecimal price, BigDecimal amount, OrgsInfoEnum orgsInfoEnum, String aKey, String sKey) {
        ResultSet<String> res = tradeAdaptor(Order.direc_buy, type, null, price, amount, orgsInfoEnum,  aKey,  sKey);
        //无论是否成功都要保存订单信息
        Order order = new Order(orgsInfoEnum.getOrgName(), type, Order.direc_buy, price, amount,  aKey,  sKey);
        order.setDate(DateUtils.now(null));
        order.setOrder_id(res.getData());
        mongoDbService.insert(order, Order.collectionName);

        dbLogService.info(DBEnum.ORDER, JSONUtils.obj2json(order));
        return res;
    }

    public ResultSet<String> sell(String type, BigDecimal price, BigDecimal amount, OrgsInfoEnum orgsInfoEnum, String aKey, String sKey) {
        ResultSet<String> r = tradeAdaptor(Order.direc_sell, type, null, price, amount, orgsInfoEnum,  aKey,  sKey);
        //无论是否成功都要保存订单信息
        Order order = new Order(orgsInfoEnum.getOrgName(), type, Order.direc_sell, price, amount,  aKey,  sKey);
        order.setDate(DateUtils.now(null));
        order.setOrder_id(r.getData());
        mongoDbService.insert(order, Order.collectionName);

        dbLogService.info(DBEnum.ORDER, JSONUtils.obj2json(order));
        return r;
    }

    public ResultSet<String> cancel(String orderId, String type, OrgsInfoEnum orgsInfoEnum, String aKey, String sKey) {
        ResultSet<String> res = tradeAdaptor(Order.direc_cancel, type, orderId,null, null, orgsInfoEnum,  aKey, sKey);
        //无论是否成功都要保存订单信息
        Order order = new Order(orgsInfoEnum.getOrgName(), type, Order.direc_cancel, null, null,  aKey, sKey);
        order.setDate(DateUtils.now(null));
        order.setOrder_id(res.getData());
        mongoDbService.insert(order, Order.collectionName);

        dbLogService.info(DBEnum.ORDER, JSONUtils.obj2json(order));
        return res;
    }

}
