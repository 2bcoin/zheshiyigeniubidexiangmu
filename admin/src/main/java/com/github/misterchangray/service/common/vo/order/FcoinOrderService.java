package com.github.misterchangray.service.common.vo.order;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.JSONUtils;
import com.github.misterchangray.common.utils.SignUtil;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;
import com.github.misterchangray.service.common.vo.order.response.fcoin.*;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by riecard on 2018/6/17.
 */
@Service
public class FcoinOrderService extends BaseApiQuery implements OrderQueryInterface {

    DecimalFormat df = new DecimalFormat("#.########");


    /**
     * symbol	无	交易对
     side	无	交易方向
     type	无	订单类型
     price	无	价格
     amount	无	下单量
     * @param ak
     * @param sk
     * @param symbol
     * @param type  sell/buy
     * @param price
     * @param amount
     * @return
     */
    @Override
    public ResultSet placeOrder(String ak, String sk, String symbol, String type, Double price, Double amount) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("symbol",symbol);
            map.put("side",type);
            map.put("type","limit");
            map.put("price",df.format(price));
            map.put("amount",df.format(amount));
            String uri = "https://api.fcoin.com/v2/orders";
            Long ts = System.currentTimeMillis();
            String sign = SignUtil.fcoinSign(ak, sk, "POST", uri, map, ts);
//            if (sign != null) {
//                return "-1";
//            }
            Map<String, String> headers = new HashMap<>();
            headers.put("FC-ACCESS-KEY",ak);
            headers.put("FC-ACCESS-SIGNATURE",sign);
            headers.put("FC-ACCESS-TIMESTAMP",ts.toString());
            String json = this.postJson(uri, map, headers);
            FcoinSimpleResponse res = JSONUtils.json2obj(json, FcoinSimpleResponse.class);
            if (res.getStatus() == 0) {
                return ResultSet.build(ResultEnum.SUCCESS).setData(res.getData());
            } else {
                if(json.contains("1016") && json.contains("account balance insufficient")) {
                    //单独处理余额不足得情况
                    return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
                }
                return ResultSet.build(ResultEnum.FAILURE).setCode(res.getStatus()).setMsg(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultSet.build().setCode(-1);
        }
    }

    @Override
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
            String uri = "https://api.fcoin.com/v2/orders/" + order_id;
            Long ts = System.currentTimeMillis();
            String sign = SignUtil.fcoinSign(ak,sk,"GET", uri, null, ts);
            Map<String, String> headers = new HashMap<>();
            headers.put("FC-ACCESS-KEY",ak);
            headers.put("FC-ACCESS-SIGNATURE",sign);
            headers.put("FC-ACCESS-TIMESTAMP",ts.toString());
            String json = this.get(uri, headers);
            FcoinOrderResponse res = mapper.readValue(json, FcoinOrderResponse.class);
            if (res!=null && res.getStatus()==0 && res.getData()!=null) {
                FcoinOrder order = res.getData();
                OrderBean orderBean = new OrderBean();
                orderBean.setAmount(order.getAmount());
                orderBean.setOrder_id(order.getId());
                orderBean.setPrice(order.getPrice());
                orderBean.setSymbol(order.getSymbol());
                orderBean.setType(order.getSide());
                if (order.getState().equals("submitted")) {
                    orderBean.setStatus(df.format(0));
                } else if (order.getState().equals("canceled")) {
                    orderBean.setStatus(df.format(-1));
                } else if (order.getState().equals("filled")) {
                    orderBean.setStatus(df.format(2));
                } else if (order.getState().equals("partial_filled") || order.getState().equals("partial_canceled")) {
                    orderBean.setStatus(df.format(1));
                } else if (order.getState().equals("pending_cancel")) {  // 失效
                    orderBean.setStatus(df.format(4));
                }
                orderBean.setField_amount(order.getFilled_amount());
                return orderBean;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResultSet<String> cancelOrder(String ak, String sk, String symbol, String order_id) {
        //POST https://api.fcoin.com/v2/orders/{order_id}/submit-cancel
        try {
            String uri = "https://api.fcoin.com/v2/orders/"+order_id+"/submit-cancel";
            Long ts = System.currentTimeMillis();
            String sign = SignUtil.fcoinSign(ak,sk,"POST", uri, null, ts);
            Map<String, String> headers = new HashMap<>();
            headers.put("FC-ACCESS-KEY",ak);
            headers.put("FC-ACCESS-SIGNATURE",sign);
            headers.put("FC-ACCESS-TIMESTAMP",ts.toString());
            String json = this.postJson(uri, new HashMap<>(), headers);
            FcoinCancleOrderResponse res = JSONUtils.json2obj(json, FcoinCancleOrderResponse.class);
            if (res.getStatus() == 0) {
                return ResultSet.build(ResultEnum.SUCCESS).setCode(res.getStatus());
            } else {
                System.out.println(res);
                return ResultSet.build(ResultEnum.FAILURE).setMsg(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultSet.build().setCode(1);
        }
    }

    /**
     * symbol		交易对
     states		订单状态
     before		查询某个页码之前的订单
     after		查询某个页码之后的订单
     limit		每页的订单数量，默认为 20 条
     * @param ak
     * @param sk
     * @param symbol
     * @return
     */
    @Override
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol) {
        // GET https://api.fcoin.com/v2/orders
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
            String uri = "https://api.fcoin.com/v2/orders?states=submitted&symbol="+symbol;
            Long ts = System.currentTimeMillis();
//            Map<String, String> map = new HashMap<>();
//            map.put("symbio", symbol);
            String sign = SignUtil.fcoinSign(ak,sk,"GET", uri, null, ts);
            Map<String, String> headers = new HashMap<>();
            headers.put("FC-ACCESS-KEY",ak);
            headers.put("FC-ACCESS-SIGNATURE",sign);
            headers.put("FC-ACCESS-TIMESTAMP",ts.toString());
            String json = this.get(uri, headers);
            FcoinOrderListResponse res = mapper.readValue(json, FcoinOrderListResponse.class);
            if (res!=null && res.getStatus()==0 && res.getData()!=null) {
                List<OrderBean> list = new ArrayList<>();
                for (FcoinOrder order:res.getData()) {
                    OrderBean orderBean = new OrderBean();
                    orderBean.setAmount(order.getAmount());
                    orderBean.setOrder_id(order.getId());
                    orderBean.setPrice(order.getPrice());
                    orderBean.setSymbol(order.getSymbol());
                    orderBean.setType(order.getSide());
                    if (order.getState().equals("submitted")) {
                        orderBean.setStatus(df.format(0));
                    } else if (order.getState().equals("canceled")) {
                        orderBean.setStatus(df.format(-1));
                    } else if (order.getState().equals("filled")) {
                        orderBean.setStatus(df.format(2));
                    } else if (order.getState().equals("partial_filled") || order.getState().equals("partial_canceled")) {
                        orderBean.setStatus(df.format(1));
                    } else if (order.getState().equals("pending_cancel")) {  // 失效
                        orderBean.setStatus(df.format(4));
                    }
                    orderBean.setField_amount(order.getFilled_amount());
                    list.add(orderBean);
                }
                return list;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
