package com.github.misterchangray.service.po;

public class KLine {
    String id;
    String org ;
    String type;
    String data1min; //一分钟得K线数据
    String data1sec; //一秒钟得K线数据


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData1min() {
        return data1min;
    }

    public void setData1min(String data1min) {
        this.data1min = data1min;
    }

    public String getData1sec() {
        return data1sec;
    }

    public void setData1sec(String data1sec) {
        this.data1sec = data1sec;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
}
