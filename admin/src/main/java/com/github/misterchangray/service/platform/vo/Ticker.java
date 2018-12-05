package com.github.misterchangray.service.platform.vo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by riecard on 2018/5/21.
 */
public class Ticker {
    private Long updateTime; //数据更新时间
    private String last;
    private String high;
    private String low;
    private String buy;
    private String sell;
    private String vol;
    private JsonNode Info; //	交易所返回的原始结构

    public JsonNode getInfo() {
        return Info;
    }

    public void setInfo(JsonNode info) {
        Info = info;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Ticker(String last, String high, String low, String buy, String sell, String vol) {
        this.setUpdateTime(System.currentTimeMillis());
        this.last = last;
        this.high = high;
        this.low = low;
        this.buy = buy;
        this.sell = sell;
        this.vol = vol;
    }

    public Ticker() { }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "updateTime=" + updateTime +
                ", last='" + last + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", buy='" + buy + '\'' +
                ", sell='" + sell + '\'' +
                ", vol='" + vol + '\'' +
                ", Info=" + Info +
                '}';
    }
}
