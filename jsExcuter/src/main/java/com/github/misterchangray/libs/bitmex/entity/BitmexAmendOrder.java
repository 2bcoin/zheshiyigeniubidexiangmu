/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 *
 * @author RobTerpilowski
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitmexAmendOrder {
    
     protected String orderID;
     protected String origCLOrdID;
     protected String clOrdID;
     protected Double simpleOrderQty;
     protected Double orderQty;
     protected Double simpleLeavesQty;
     protected Double leavesQty;
     protected Double price;
     protected Double stopPx;
     protected Double pegOffsetValue;
     protected String text;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrigCLOrdID() {
        return origCLOrdID;
    }

    public void setOrigCLOrdID(String origCLOrdID) {
        this.origCLOrdID = origCLOrdID;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public void setClOrdID(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public Double getSimpleOrderQty() {
        return simpleOrderQty;
    }

    public void setSimpleOrderQty(Double simpleOrderQty) {
        this.simpleOrderQty = simpleOrderQty;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
    }

    public Double getSimpleLeavesQty() {
        return simpleLeavesQty;
    }

    public void setSimpleLeavesQty(Double simpleLeavesQty) {
        this.simpleLeavesQty = simpleLeavesQty;
    }

    public Double getLeavesQty() {
        return leavesQty;
    }

    public void setLeavesQty(Double leavesQty) {
        this.leavesQty = leavesQty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getStopPx() {
        return stopPx;
    }

    public void setStopPx(Double stopPx) {
        this.stopPx = stopPx;
    }

    public Double getPegOffsetValue() {
        return pegOffsetValue;
    }

    public void setPegOffsetValue(Double pegOffsetValue) {
        this.pegOffsetValue = pegOffsetValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



   

    @Override
    public String toString() {
        return "BitmexAmendOrder{" + "orderID=" + orderID + ", origCLOrdID=" + origCLOrdID + ", clOrdID=" + clOrdID + ", simpleOrderQty=" + simpleOrderQty + ", orderQty=" + orderQty + ", simpleLeavesQty=" + simpleLeavesQty + ", leavesQty=" + leavesQty + ", price=" + price + ", stopPx=" + stopPx + ", pegOffsetValue=" + pegOffsetValue + ", text=" + text + '}';
    }
     
     
    
}
