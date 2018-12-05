package com.github.misterchangray.service.common.vo.order;


import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.libs.huobi.api.HuobiApiClient;
import com.github.misterchangray.libs.huobi.request.CreateOrderRequest;
import com.github.misterchangray.libs.huobi.request.IntrustOrdersDetailRequest;
import com.github.misterchangray.libs.huobi.response.*;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class HuobiOrderService implements OrderQueryInterface {
    @Override
    public ResultSet<String> placeOrder(String ak, String sk, String symbol, String type, Double price, Double amount) {
        HuobiApiClient huobiApiClient = new HuobiApiClient(ak, sk);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<Account> accounts = huobiApiClient.getAccounts();
        DecimalFormat df = new DecimalFormat("#.########");
        for (Account account : accounts) {
//            System.out.println(account);
            if (account.getType().equals("spot")) {
                createOrderRequest.setAccountId(df.format(account.getId()));
            }
        }
        //将double类型转为String类型
        String amoutstr = df.format(amount);
        createOrderRequest.setAmount(amoutstr);
        String pricestr = df.format(price);
        createOrderRequest.setPrice(pricestr);
        createOrderRequest.setSymbol(symbol);
        if (type.equals("buy")) {
            //限价买入
            createOrderRequest.setType(CreateOrderRequest.OrderType.BUY_LIMIT);
        } else if (type.equals("sell")) {
            //限价卖出
            createOrderRequest.setType(CreateOrderRequest.OrderType.SELL_LIMIT);
        }
        Long orderID = null;
        String orderIdStr = null;
        try {
            //创建订单，返回订单号
             orderID = huobiApiClient.createOrder(createOrderRequest);
            //根据订单号执行订单
             orderIdStr = huobiApiClient.placeOrder(orderID);
        } catch (Exception e) {
            //com.github.misterchangray.libs.huobi.api.ApiException: trade account balance is not enough, left: `2379.9003`
            if(e.toString().contains("trade account balance is not enough")) {
                //单独处理余额不足
                return ResultSet.build(ResultEnum.AccountBalanceInsufficient);
            }
            return ResultSet.build(ResultEnum.FAILURE).setMsg(e.toString());
        }
        return ResultSet.build(ResultEnum.SUCCESS).setData(orderIdStr);
    }

    @Override
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id) {
        HuobiApiClient huobiApiClient = new HuobiApiClient(ak, sk);
        OrdersDetailResponse<OrdersDetail> ordersDetailResponse = huobiApiClient.ordersDetail(order_id);
        OrdersDetail ordersDetail = ordersDetailResponse.getData();
        OrderBean orderBean = new OrderBean();
        orderBean.setSymbol(ordersDetail.getSymbol());
        orderBean.setPrice(ordersDetail.getPrice());
        orderBean.setAmount(ordersDetail.getAmount());
        orderBean.setOrder_id(String.valueOf(ordersDetail.getId()));
        orderBean.setCreated_at(String.valueOf(ordersDetail.getCreatedat()));
        orderBean.setCanceled_at(String.valueOf(ordersDetail.getCanceledat()));
        orderBean.setField_amount(ordersDetail.getFieldamount());
        orderBean.setField_cash_amount(ordersDetail.getFieldcashamount());
        orderBean.setField_fees(ordersDetail.getFieldfees());
        orderBean.setFinishe_at(String.valueOf(ordersDetail.getFieldfees()));
        if (ordersDetail.getType().equals("buy-limit") || ordersDetail.getType().equals("buy-market")) {
            orderBean.setType("buy");
        } else if (ordersDetail.getType().equals("sell-limit") || ordersDetail.getType().equals("sell-market")) {
            orderBean.setType("sell");
        }
        if (ordersDetail.getState().equals("submitting") || ordersDetail.getState().equals("submitted")) {
            orderBean.setStatus(String.valueOf(0));
        } else if (ordersDetail.getState().equals("partial-filled")) {
            orderBean.setStatus(String.valueOf(1));
        } else if (ordersDetail.getState().equals("filled")) {
            orderBean.setStatus(String.valueOf(2));
        } else if (ordersDetail.getState().equals("canceled")) {
            orderBean.setStatus(String.valueOf(-1));
        } else if (ordersDetail.getState().equals("partial-canceled")) {
            orderBean.setStatus(String.valueOf(4));
        }
        if (orderBean != null) {
            return orderBean;
        }
        return null;
    }

    @Override
    public ResultSet<String> cancelOrder(String ak, String sk, String symbol, String order_id) {
        HuobiApiClient huobiApiClient = new HuobiApiClient(ak, sk);
        SubmitcancelResponse submitcancelResponse = huobiApiClient.submitcancel(order_id);
        if (submitcancelResponse.getStatus().equals("ok")) {
            return ResultSet.build(ResultEnum.SUCCESS);
        }
        return ResultSet.build(ResultEnum.FAILURE);
    }

    /**
     * 查询历史交易记录
     *
     * @param ak
     * @param sk
     * @param symbol
     * @return
     */
    @Override
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol) {
        HuobiApiClient huobiApiClient = new HuobiApiClient(ak, sk);
        IntrustOrdersDetailRequest intrustOrdersDetailRequest = new IntrustOrdersDetailRequest();
        intrustOrdersDetailRequest.setSymbol(symbol);
        intrustOrdersDetailRequest.setStates(IntrustOrdersDetailRequest.OrderStates.FILLED + "," + IntrustOrdersDetailRequest.OrderStates.CANCELED
                + "," + IntrustOrdersDetailRequest.OrderStates.PARTIAL_CANCELED + "," + IntrustOrdersDetailRequest.OrderStates.PARTIAL_FILLED
                + "," + IntrustOrdersDetailRequest.OrderStates.PRE_SUBMITTED + "," + IntrustOrdersDetailRequest.OrderStates.SUBMITTED);
        IntrustDetailResponse< List<OrdersDetail>> intrustDetailResponse = huobiApiClient.intrustOrdersDetail(intrustOrdersDetailRequest);
        List<OrdersDetail> list = intrustDetailResponse.getData();
        List<OrderBean> orderBeanList = new ArrayList<>();
        if (orderBeanList != null) {
            for (OrdersDetail ordersDetail : list) {
                OrderBean orderBean = new OrderBean();
                orderBean.setSymbol(ordersDetail.getSymbol());
                orderBean.setPrice(ordersDetail.getPrice());
                orderBean.setAmount(ordersDetail.getAmount());
                orderBean.setOrder_id(String.valueOf(ordersDetail.getId()));
                orderBean.setCreated_at(String.valueOf(ordersDetail.getCreatedat()));
                orderBean.setCanceled_at(String.valueOf(ordersDetail.getCanceledat()));
                orderBean.setField_amount(ordersDetail.getFieldamount());
                orderBean.setField_cash_amount(ordersDetail.getFieldcashamount());
                orderBean.setField_fees(ordersDetail.getFieldfees());
                orderBean.setFinishe_at(String.valueOf(ordersDetail.getFieldfees()));
                if (ordersDetail.getType().equals("buy-limit") || ordersDetail.getType().equals("buy-market")) {
                    orderBean.setType("buy");
                } else if (ordersDetail.getType().equals("sell-limit") || ordersDetail.getType().equals("sell-market")) {
                    orderBean.setType("sell");
                }
                if (ordersDetail.getState().equals("submitting") || ordersDetail.getState().equals("submitted")) {
                    orderBean.setStatus(String.valueOf(0));
                } else if (ordersDetail.getState().equals("partial-filled")) {
                    orderBean.setStatus(String.valueOf(1));
                } else if (ordersDetail.getState().equals("filled")) {
                    orderBean.setStatus(String.valueOf(2));
                } else if (ordersDetail.getState().equals("canceled")) {
                    orderBean.setStatus(String.valueOf(-1));
                } else if (ordersDetail.getState().equals("partial-canceled")) {
                    orderBean.setStatus(String.valueOf(4));
                }
                orderBeanList.add(orderBean);
            }
        }
        for (OrderBean orderBean : orderBeanList) {
            System.out.println(orderBean);
        }
        return orderBeanList;
    }

}
