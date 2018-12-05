package com.github.misterchangray.service.common.vo.order.response;

import java.util.List;

public class OrderResponse {
    private String result;
    private List<OkexOrderBean> orders;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<OkexOrderBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OkexOrderBean> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "result='" + result + '\'' +
                ", orders=" + orders +
                '}';
    }
}
