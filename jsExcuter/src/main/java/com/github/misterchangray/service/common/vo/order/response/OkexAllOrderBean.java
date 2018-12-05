package com.github.misterchangray.service.common.vo.order.response;

import java.util.List;

public class OkexAllOrderBean {
    private String currency_page;
    private String page_length;
    private String result;
    private String total;
    private List<OkexOrderBean> orders;

    public String getCurrency_page() {
        return currency_page;
    }

    public void setCurrency_page(String currency_page) {
        this.currency_page = currency_page;
    }

    public String getPage_length() {
        return page_length;
    }

    public void setPage_length(String page_length) {
        this.page_length = page_length;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<OkexOrderBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OkexOrderBean> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "OkexAllOrderBean{" +
                "currency_page='" + currency_page + '\'' +
                ", page_length='" + page_length + '\'' +
                ", result='" + result + '\'' +
                ", total='" + total + '\'' +
                ", orders=" + orders +
                '}';
    }
}
