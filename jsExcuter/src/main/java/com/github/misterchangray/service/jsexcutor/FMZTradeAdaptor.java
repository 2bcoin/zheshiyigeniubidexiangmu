package com.github.misterchangray.service.jsexcutor;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.CoinSpot;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.HttpUtilManager;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.controller.common.OrgsInfoEnum;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.jsexcutor.exception.FMZException;
import com.github.misterchangray.service.jsexcutor.util.Logger;
import com.github.misterchangray.service.jsexcutor.util.Utils;
import com.github.misterchangray.service.jsexcutor.vo.FMZOrder;
import com.github.misterchangray.service.platform.vo.Order;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FMZTradeAdaptor {
    private FMZAdaptor fmzAdaptor;

    ///////////////////////////////////////////////////交易操作
    /////////////////////////////////////////////////////////////////////////////io///////////////////////////////////
    public String Buy(Double Price, Double Amount) {
        if(fmzAdaptor.canTrade()) return null;
        Price = fmzAdaptor.formatPrice(Price);
        Amount = fmzAdaptor.formatAmount(Amount);

        ResultSet<String> resultSet = ResultSet.build(ResultEnum.FAILURE);
        try {
            if(fmzAdaptor.isConfig__Otc()) {
                if(null != fmzAdaptor.getConfig__ContractType() && null != fmzAdaptor.getConfig__Coin()) {
                    BaseService baseService = fmzAdaptor.getConfig__Service();

                    resultSet = baseService.futureOrder(CoinOtc.getCoin( fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                            fmzAdaptor.getConfig__Direction(), fmzAdaptor.getConfig__MarginLevel(),
                            Utils.convertBigDecimal(Price), Utils.convertBigDecimal(Amount),
                            fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());

                    if(resultSet.isSuccess()) {
                        fmzAdaptor.log(Logger.Level.Buy,
                                MessageFormat.format("'{'\"type\":\"{0}\", \"direct\":\"{1}\", \"contract\":\"{2}\", \"price\":\"{3}\", \"amount\":\"{4}\", \"org\":\"{5}\", \"coin\":\"{6}\"'}'",
                                        "买入", fmzAdaptor.getConfig__Direction(), fmzAdaptor.getConfig__ContractType(), Price, Amount, fmzAdaptor.getConfig__Org(), fmzAdaptor.getConfig__Coin()));

                        return resultSet.getData();
                    }
                    throw new FMZException(resultSet.getMsg() + resultSet.getData());
                }

            } else {
                BaseService baseService = fmzAdaptor.getConfig__Service();
                resultSet = baseService.buy(CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), Utils.convertBigDecimal(Price),
                        Utils.convertBigDecimal(Amount), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
                if(resultSet.isSuccess()) {
                    fmzAdaptor.log(Logger.Level.Buy,
                            MessageFormat.format("'{'\"type\":\"{0}\", \"direct\":\"{1}\", \"contract\":\"{2}\", \"price\":\"{3}\", \"amount\":\"{4}\", \"org\":\"{5}\", \"coin\":\"{6}\"'}'",
                                    "买入", null, null, Price, Amount, fmzAdaptor.getConfig__Org(), fmzAdaptor.getConfig__Coin()));

                    return resultSet.getData();
                }
                throw new FMZException(resultSet.getMsg() + resultSet.getData());
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return resultSet.getData();
    }

    public String Sell(Double Price, Double Amount) {
        if(fmzAdaptor.canTrade()) return null;
        Price = fmzAdaptor.formatPrice(Price);
        Amount = fmzAdaptor.formatAmount(Amount);

        ResultSet<String> resultSet = ResultSet.build(ResultEnum.FAILURE);
        try {

            if(fmzAdaptor.isConfig__Otc()) {
                if(null != fmzAdaptor.getConfig__ContractType() && null != fmzAdaptor.getConfig__Coin()) {
                    BaseService baseService = fmzAdaptor.getConfig__Service();

                    resultSet = baseService.futureOrder(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                            fmzAdaptor.getConfig__Direction(), fmzAdaptor.getConfig__MarginLevel(),
                            Utils.convertBigDecimal(Price), Utils.convertBigDecimal(Amount),
                            fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());

                    if(resultSet.isSuccess()) {
                        fmzAdaptor.log(Logger.Level.Sell,
                                MessageFormat.format("'{'\"type\":\"{0}\", \"direct\":\"{1}\", \"contract\":\"{2}\", \"price\":\"{3}\", \"amount\":\"{4}\", \"org\":\"{5}\", \"coin\":\"{6}\"'}'",
                                        "卖出", fmzAdaptor.getConfig__Direction(), fmzAdaptor.getConfig__ContractType(), Price, Amount, fmzAdaptor.getConfig__Org(), fmzAdaptor.getConfig__Coin()));

                        return resultSet.getData();
                    }
                    throw new FMZException(resultSet.getMsg()  + resultSet.getData());
                }
            } else {
                BaseService baseService = fmzAdaptor.getConfig__Service();
                resultSet = baseService.sell(CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), Utils.convertBigDecimal(Price),
                        Utils.convertBigDecimal(Amount), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());

                if(resultSet.isSuccess()) {
                    fmzAdaptor.log(Logger.Level.Sell,
                            MessageFormat.format("'{'\"type\":\"{0}\", \"direct\":\"{1}\", \"contract\":\"{2}\", \"price\":\"{3}\", \"amount\":\"{4}\", \"org\":\"{5}\", \"coin\":\"{6}\"'}'",
                                    "卖出", null, null, Price, Amount, fmzAdaptor.getConfig__Org(), fmzAdaptor.getConfig__Coin()));

                    return resultSet.getData();
                }
                throw new FMZException(resultSet.getMsg()  + resultSet.getData());
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return resultSet.getData();
    }

    public String CancelOrder(String orderId) {
        if(fmzAdaptor.canTrade()) return null;
        BaseService baseService = fmzAdaptor.getConfig__Service();
        ResultSet<String> resultSet = ResultSet.build(ResultEnum.FAILURE);
        try {
            if(fmzAdaptor.isConfig__Otc()) {
                resultSet =  baseService.cancelOrderFuture(orderId, CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                        fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            } else {
                resultSet = baseService.cancelOrder(orderId, CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            }
            if(resultSet.isSuccess()) {
                fmzAdaptor.log(Logger.Level.CancelOrder, MessageFormat.format("CancelOrder;Id:{0}", orderId));
            } else {
                throw new FMZException(resultSet.getMsg()  + resultSet.getData());
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return resultSet.getData();
    }

    public String GetOrder(String orderId) {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        try {
            Order order = null;
            if(fmzAdaptor.isConfig__Otc()) {
                order = baseService.getOrderOtc(orderId, CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(),
                        fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            } else {
                order = baseService.getOrderSpot(orderId, CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            }
            if(null != order) {
                FMZOrder fmzOrder = new FMZOrder();
                fmzOrder.Info = order.getInfo();
                fmzOrder.Amount = Utils.convertBigDecimal(order.getAmount());
                fmzOrder.Id = order.getOrder_id();
                fmzOrder.Status = Integer.parseInt(order.getStatus());
                fmzOrder.Type = Integer.parseInt(order.getType());
                fmzOrder.Price = Utils.convertBigDecimal(order.getPrice());

                return JSONUtils.obj2json(fmzOrder);
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return null;
    }

    public String GetOrders() {
        BaseService baseService = fmzAdaptor.getConfig__Service();
        try {
            List<Order> list = null;
            if(fmzAdaptor.isConfig__Otc()) {
                list = baseService.getOrdersOtc(CoinOtc.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__ContractType(), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            } else {
                list = baseService.getOrdersSpot(CoinSpot.getCoin(fmzAdaptor.getConfig__Coin()), fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
            }
            if(null != list) {
                List<FMZOrder > fmzOrders = new ArrayList<>();
                for(Order o : list) {
                    FMZOrder fmzOrder = new FMZOrder();
                    fmzOrder.Info = o.getInfo();
                    fmzOrder.Amount = Utils.convertBigDecimal(o.getAmount());
                    fmzOrder.Id = o.getOrder_id();
                    fmzOrder.Status = Integer.parseInt(o.getStatus());
                    fmzOrder.Type = Integer.parseInt(o.getType());
                    fmzOrder.Price = Utils.convertBigDecimal(o.getPrice());
                    fmzOrders.add(fmzOrder);
                }
                return JSONUtils.obj2json(fmzOrders);
            }
        } catch (Exception e) {
            fmzAdaptor.log(Logger.Level.Warn, e.toString());
        }
        return null;
    }


    public String IO_Rest(String method, String api, Map<String, String> param) {
        OrgsInfoEnum orgsInfoEnum = OrgsInfoEnum.getOrgsInfoEnumByName(fmzAdaptor.getConfig__Org());
        if(null != orgsInfoEnum) {
            if(fmzAdaptor.isConfig__Otc()) {
                api = orgsInfoEnum.getOrgRestUrlOtc() + api;
            } else {
                api = orgsInfoEnum.getOrgRestUrlSpot() + api;
            }
            BaseService baseService = fmzAdaptor.getConfig__Service();
            try {
                if(null != baseService) {
                    return baseService.customRequest(method, api, param, fmzAdaptor.getConfig__AKEY(), fmzAdaptor.getConfig__SKEY());
                }
            } catch (Exception e) {
                fmzAdaptor.log(Logger.Level.Warn, e.toString());
            }
        }
        return null;
    }


    public FMZTradeAdaptor(FMZAdaptor fmzAdaptor) {
        this.fmzAdaptor = fmzAdaptor;
    }

}
