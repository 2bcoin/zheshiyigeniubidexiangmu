package com.github.misterchangray.service.platform.vo;


import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;

public class Order implements Serializable {
    private String order_id;
    private String side; //sell OR buy
    private String amount;
    private String price;
    private String status;  // -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中 4:部分成交撤销
    private String symbol;
    private String contractType; //期货交易 ，季度或者当周
    private String type; //订单类型 1现货;2期货
    private String leverRate; //杠杆倍数
    private String futureType;//期货 1：开多 2：开空 3：平多 4： 平空
    private JsonNode Info; //	交易所返回的原始结构

    public String getFutureType() {
        return futureType;
    }

    public void setFutureType(String futureType) {
        this.futureType = futureType;
    }

    public String getLeverRate() {
        return leverRate;
    }

    public void setLeverRate(String leverRate) {
        this.leverRate = leverRate;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public JsonNode getInfo() {
        return Info;
    }

    public void setInfo(JsonNode info) {
        Info = info;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id='" + order_id + '\'' +
                ", side='" + side + '\'' +
                ", amount='" + amount + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", symbol='" + symbol + '\'' +
                ", contractType='" + contractType + '\'' +
                ", type='" + type + '\'' +
                ", leverRate='" + leverRate + '\'' +
                ", futureType='" + futureType + '\'' +
                ", Info=" + Info +
                '}';
    }
}
