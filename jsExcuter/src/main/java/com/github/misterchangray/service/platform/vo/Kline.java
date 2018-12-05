package com.github.misterchangray.service.platform.vo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @Author riecard
 * @Date 2018/5/20
 * @Time 11:35
 */

public class Kline {
    private long time;
    private double open;
    private double close;
    private double low;
    private double high;
    private double vol;
    private JsonNode Info; //	交易所返回的原始结构

    public JsonNode getInfo() {
        return Info;
    }

    public void setInfo(JsonNode info) {
        Info = info;
    }

    public Kline(long time, double open, double close, double low, double high, double vol) {
        this.time = time;
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        this.vol = vol;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        return "Kline{" +
                "time=" + time +
                ", open=" + open +
                ", close=" + close +
                ", low=" + low +
                ", high=" + high +
                ", vol=" + vol +
                '}';
    }
}
