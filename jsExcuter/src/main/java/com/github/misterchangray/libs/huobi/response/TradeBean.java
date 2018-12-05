package com.github.misterchangray.libs.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:57
 */

public class TradeBean {

    /**
     * id : 600848670
     * price : 7962.62
     * amount : 0.0122
     * direction : buy
     * ts : 1489464451000
     */

    private long id;
    private double price;
    private double amount;
    private String direction;
    private long ts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }


}
