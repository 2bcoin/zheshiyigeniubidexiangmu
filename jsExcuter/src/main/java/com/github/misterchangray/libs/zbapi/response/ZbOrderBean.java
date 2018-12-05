package com.github.misterchangray.libs.zbapi.response;

public class ZbOrderBean {
    private Double total_amount;
    private String id;
    private Double price;
    private Long trade_date;
    private Integer status;
    private Double trade_money;
    private Double trade_amount;
    private Integer type;
    private String currency;

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getTrade_date() {
        return trade_date;
    }

    public void setTrade_date(Long trade_date) {
        this.trade_date = trade_date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getTrade_money() {
        return trade_money;
    }

    public void setTrade_money(Double trade_money) {
        this.trade_money = trade_money;
    }

    public Double getTrade_amount() {
        return trade_amount;
    }

    public void setTrade_amount(Double trade_amount) {
        this.trade_amount = trade_amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ZbOrderBean{" +
                "total_amount=" + total_amount +
                ", id='" + id + '\'' +
                ", price=" + price +
                ", trade_date=" + trade_date +
                ", status=" + status +
                ", trade_money=" + trade_money +
                ", trade_amount=" + trade_amount +
                ", type=" + type +
                ", currency='" + currency + '\'' +
                '}';
    }
}
