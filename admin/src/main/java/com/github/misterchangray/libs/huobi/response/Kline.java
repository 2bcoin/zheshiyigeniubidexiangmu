package com.github.misterchangray.libs.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 11:35
 */

public class Kline {


    private long id;
    private double amount;
    private int count;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }


    public void setClose(int close) {
        this.close = close;
    }


    public void setLow(int low) {
        this.low = low;
    }


    public void setHigh(int high) {
        this.high = high;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }
}
