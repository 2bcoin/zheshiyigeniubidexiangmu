package com.github.misterchangray.service.common.vo.order;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.libs.binance.api.client.domain.TimeInForce;
import com.github.misterchangray.libs.binance.api.client.domain.account.NewOrder;
import com.github.misterchangray.libs.binance.api.client.domain.account.NewOrderResponse;
import com.github.misterchangray.libs.binance.api.client.domain.account.Order;
import com.github.misterchangray.libs.binance.api.client.domain.account.request.AllOrdersRequest;
import com.github.misterchangray.libs.binance.api.client.domain.account.request.CancelOrderRequest;
import com.github.misterchangray.libs.binance.api.client.domain.account.request.OrderStatusRequest;
import com.github.misterchangray.libs.binance.api.client.impl.BinanceApiRestClientImpl;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class BianOrderService implements OrderQueryInterface {

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


        //将double类型转为String类型
        DecimalFormat df = new DecimalFormat("#.########");
        String pricestr = df.format(price);

        BinanceApiRestClientImpl client = new BinanceApiRestClientImpl(ak, sk);
        NewOrder newOrder = null;
        if (type.equals("buy")) {
            newOrder = NewOrder.limitBuy(symbol, TimeInForce.GTC, amount.toString(), pricestr);
        } else if (type.equals("sell")) {
            newOrder = NewOrder.limitSell(symbol, TimeInForce.GTC, amount.toString(), pricestr);
        }
        NewOrderResponse newOrderResponse;
        try {
            newOrderResponse = client.newOrder(newOrder);
        } catch (Exception e) {
            if(e.toString().contains("Account has insufficient balance")) {
                return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
            }
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
        return ResultSet.build(ResultEnum.SUCCESS).setData(newOrderResponse.getClientOrderId());
    }

    public static void main(String[] args) {
        new BianOrderService().placeOrder("UlHJVCYe8vHO6n7dOdoLg9c57GStVOWSF8o0k8I6Gqs0giUB41wDYQn7bx1zwN3a","WWsEIwzEdloxf5C56IR8sBsyOsLNenDyvY0ULELDmUkthmSVy7Dc1WfB0zmB1jQe",
                "IOTABTC","buy",0.00018341D,10D);
    }

    /**
     * 获取订单消息
     *
     * @param ak
     * @param sk
     * @param symbol   交易对
     * @param order_id 订单ID
     * @return
     */
    @Override
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id) {
        BinanceApiRestClientImpl client = new BinanceApiRestClientImpl(ak, sk);
        DecimalFormat df = new DecimalFormat("#.########");
        if (StringUtils.isNotBlank(order_id)) {
            Order order = client.getOrderStatus(new OrderStatusRequest(symbol, order_id));

            OrderBean orderBean = new OrderBean();
            orderBean.setAmount(order.getOrigQty());
            orderBean.setOrder_id(order.getClientOrderId());
            orderBean.setPrice(order.getPrice());
            orderBean.setSymbol(order.getSymbol());
            if (order.getSide().name().equals("BUY")) {
                orderBean.setType("buy");
            } else if (order.getSide().name().equals("SELL")) {
                orderBean.setType("sell");
            }
            if (order.getStatus().name().equals("NEW")) {
                orderBean.setStatus(df.format(0));
            } else if (order.getStatus().name().equals("CANCELED")) {
                orderBean.setStatus(df.format(-1));
            } else if (order.getStatus().name().equals("FILLED")) {
                orderBean.setStatus(df.format(2));
            } else if (order.getStatus().name().equals("PARTIALLY_FILLED")) {
                orderBean.setStatus(df.format(1));
            } else if (order.getStatus().name().equals("EXPIRED")) {  // 失效
                orderBean.setStatus(df.format(4));
            } else if (order.getStatus().name().equals("REJECTED")) {  // 拒绝
                orderBean.setStatus(df.format(3));
            }
            orderBean.setField_amount(order.getExecutedQty());

            return orderBean;
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
        if(null == order_id) return ResultSet.build(ResultEnum.FAILURE).setMsg("orderId 不能为空");
        BinanceApiRestClientImpl client = new BinanceApiRestClientImpl(ak, sk);
        client.cancelOrder(new CancelOrderRequest(symbol, order_id));
        Order order = client.getOrderStatus(new OrderStatusRequest(symbol, order_id));
        if (order.getStatus().name().equals("CANCELED")) {
            return ResultSet.build().setData("true").setCode(0);
        }
        return ResultSet.build().setCode(1);
    }

    @Override
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol) {
        BinanceApiRestClientImpl client = new BinanceApiRestClientImpl(ak, sk);
        AllOrdersRequest allOrdersRequest = new AllOrdersRequest(symbol);
        DecimalFormat df = new DecimalFormat("#.########");
        List<Order> order = client.getAllOrders(allOrdersRequest);
        List<OrderBean> orderBeanList = new ArrayList<>();
        for (Order order1 : order) {
            OrderBean orderBean = new OrderBean();
            orderBean.setAmount(order1.getOrigQty());
            orderBean.setPrice(order1.getPrice());
            orderBean.setOrder_id(order1.getClientOrderId());
            orderBean.setSymbol(order1.getSymbol());
            if (order1.getSide().name().equals("BUY")) {
                orderBean.setType("buy");
            } else if (order1.getSide().name().equals("SELL")) {
                orderBean.setType("sell");
            }
            if (order1.getStatus().name().equals("NEW")) {
                orderBean.setStatus(df.format(0));
            } else if (order1.getStatus().name().equals("CANCELED")) {
                orderBean.setStatus(df.format(-1));
            } else if (order1.getStatus().name().equals("FILLED")) {
                orderBean.setStatus(df.format(2));
            }
            orderBean.setField_amount(order1.getExecutedQty());
            orderBeanList.add(orderBean);
        }
        for (OrderBean orderBean : orderBeanList) {
            System.out.println(orderBean);
        }
        return orderBeanList;
    }
}
