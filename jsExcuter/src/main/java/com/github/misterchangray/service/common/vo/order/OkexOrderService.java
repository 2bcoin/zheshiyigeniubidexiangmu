package com.github.misterchangray.service.common.vo.order;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.SignUtil;
import com.github.misterchangray.service.common.vo.order.response.OkexAllOrderBean;
import com.github.misterchangray.service.common.vo.order.response.OkexOrderBean;
import com.github.misterchangray.service.common.vo.order.response.OkexOrderIDBean;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OkexOrderService extends BaseApiQuery implements OrderQueryInterface {
    Logger logger = LoggerFactory.getLogger(OkexOrderService.class);
    /**
     * 下单
     *
     * @param ak
     * @param sk
     * @param symbol
     * @param type   sell/buy
     * @param price
     * @param amount
     * @return
     */
    @Override
    public ResultSet<String> placeOrder(String ak, String sk, String symbol, String type, Double price, Double amount) {

        Map<String, String> map = new HashMap<>();
        map.put("api_key", ak);
        map.put("symbol", symbol);
        map.put("type", type);
        map.put("price", price.toString());
        map.put("amount", amount.toString());
        String sign = SignUtil.okexSign(sk, map);
        map.put("sign", sign.toUpperCase());
        String s = this.post("https://www.okex.com/api/v1/trade.do", null, map, null);
        JsonNode jsonNode = JSONUtils.buildJsonNode(s);
        OkexOrderIDBean orderIDBean = JSONUtils.json2obj(s, OkexOrderIDBean.class);
        if (orderIDBean != null && null == jsonNode.get("error_code")) {
            return ResultSet.build(ResultEnum.SUCCESS).setData(orderIDBean.getOrder_id());
        } else {
            if(1002 == jsonNode.get("error_code").asInt()) {
                //余额不足
                return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
            }
            return ResultSet.build(ResultEnum.FAILURE).setCode(jsonNode.get("error_code").asInt()).setMsg(s);
        }
    }

    /**
     * 查询订单
     *
     * @param ak
     * @param sk
     * @param symbol   交易对
     * @param order_id 订单ID
     * @return
     */
    @Override
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id) {
        List<OrderBean> list = getAllOrder(ak, sk, symbol);
        if (list != null) {
            for (OrderBean orderBean:list) {
                if (order_id.equals(orderBean.getOrder_id())) {
                    return orderBean;
                }
            }
        }
        return null;
    }

    /**
     * 撤销订单
     *
     * @param ak
     * @param sk
     * @param symbol
     * @param order_id
     * @return
     */
    @Override
    public ResultSet<String> cancelOrder(String ak, String sk, String symbol, String order_id) {
        Map<String, String> map = new HashMap<>();
        map.put("api_key", ak);
        map.put("symbol", symbol);
        map.put("order_id", order_id);
        String sign = SignUtil.okexSign(sk, map);
        map.put("sign", sign.toUpperCase());
        String s = this.post("https://www.okex.com/api/v1/cancel_order.do", null, map, null);
        OkexOrderIDBean orderIDBean = JSONUtils.json2obj(s, OkexOrderIDBean.class);
        if(null == orderIDBean || null == orderIDBean.getResult()) {
            return ResultSet.build(ResultEnum.FAILURE);
        }
        return ResultSet.build(ResultEnum.SUCCESS);
    }

    @Override
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol) {
        DecimalFormat df = new DecimalFormat("0.########");
        Map<String, String> map = new HashMap<>();
        map.put("api_key", ak);
        map.put("symbol", symbol);
        map.put("status", String.valueOf(0));
        map.put("current_page", String.valueOf(1));
        map.put("page_length", String.valueOf(200));
        String sign = SignUtil.okexSign(sk, map);
        map.put("sign", sign.toUpperCase());
        String s = this.post("https://www.okex.com/api/v1/order_history.do", null, map, null);
        OkexAllOrderBean okexAllOrderBean = JSONUtils.json2obj(s, OkexAllOrderBean.class);
        List<OkexOrderBean> okexOrderBeanList1 = okexAllOrderBean.getOrders();

        Map<String, String> map2 = new HashMap<>();
        map2.put("api_key", ak);
        map2.put("symbol", symbol);
        map2.put("status", String.valueOf(1));
        map2.put("current_page", String.valueOf(1));
        map2.put("page_length", String.valueOf(200));
        String sign2 = SignUtil.okexSign(sk, map2);
        map2.put("sign", sign2.toUpperCase());
        String s1 = this.post("https://www.okex.com/api/v1/order_history.do", null, map2, null);
        OkexAllOrderBean okexAllOrderBean2 = JSONUtils.json2obj(s1, OkexAllOrderBean.class);
        List<OkexOrderBean> okexOrderBeanList2 = okexAllOrderBean2.getOrders();

        List<OkexOrderBean> okexOrderBeanList = new ArrayList<>();
        okexOrderBeanList.addAll(okexOrderBeanList1);
        okexOrderBeanList.addAll(okexOrderBeanList2);

        List<OrderBean> orderBeanList = new ArrayList<>();
        for (OkexOrderBean okexOrderBean : okexOrderBeanList) {
            OrderBean orderBean = new OrderBean();
            orderBean.setPrice(df.format(okexOrderBean.getPrice()));
            orderBean.setAmount(String.valueOf(okexOrderBean.getAmount()));
            orderBean.setSymbol(okexOrderBean.getSymbol());
            if (okexOrderBean.getType().equals("buy") || okexOrderBean.getType().equals("buy_market")) {
                orderBean.setType("buy");
            } else if (okexOrderBean.getType().equals("sell") || okexOrderBean.getType().equals("sell_market")) {
                orderBean.setType("sell");
            }
            orderBean.setCreated_at(String.valueOf(okexOrderBean.getCreate_date()));
            orderBean.setOrder_id(String.valueOf(okexOrderBean.getOrder_id()));
            orderBean.setStatus(String.valueOf(okexOrderBean.getStatus()));
            orderBeanList.add(orderBean);
        }
        return orderBeanList;
    }
}
