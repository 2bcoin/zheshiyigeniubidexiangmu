package com.github.misterchangray.service.common.vo.order;


import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.service.common.vo.order.response.OrderBean;

import java.util.List;

/**
 * Created by riecard on 2018/6/4.
 */
public interface OrderQueryInterface {

    /**
     * 下单
     * @param ak
     * @param sk
     * @param symbol
     * @param type  sell/buy
     * @param price
     * @param amount
     * @return 订单ID
     * 成功时返回订单ID，失败时返回"-1"
     */
    public ResultSet<String> placeOrder(String ak, String sk, String symbol, String type, Double price, Double amount);


    /**
     * 查看订单信息
     * @param symbol  交易对
     * @param order_id  订单ID
     * @return
     * status;  // -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中
     */
    public OrderBean getOrder(String ak, String sk, String symbol, String order_id);

    /**
     * 撤销订单
     * @param symbol
     * @param order_id
     * @return
     */
    public  ResultSet<String> cancelOrder(String ak, String sk, String symbol, String order_id);

    /**
     * 历史所有订单
     */
    public List<OrderBean> getAllOrder(String ak, String sk, String symbol);

}
