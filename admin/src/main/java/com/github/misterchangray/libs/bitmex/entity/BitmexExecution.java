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
public class BitmexExecution {

    /**
     *    /**
     * "lastLiquidityInd": "string", "simpleOrderQty": 0, "settlCurrency":
     * "string", "execType": "string", "ordType": "string", "timeInForce":
     * "string", "execInst": "string", "contingencyType": "string",
     * "exDestination": "string", "ordStatus": "string", "triggered": "string",
     * "workingIndicator": true, "ordRejReason": "string", "simpleLeavesQty": 0,
     * "leavesQty": 0, "simpleCumQty": 0, "cumQty": 0, "avgPx": 0, "commission":
     * 0, "tradePublishIndicator": "string", "multiLegReportingType": "string",
     * "text": "string", "trdMatchID": "string", "execCost": 0, "execComm": 0,
     * "homeNotional": 0, "foreignNotional": 0, "transactTime":
     * "2018-07-13T12:22:31.888Z", "timestamp": "2018-07-13T12:22:31.888Z"
     */
    protected String execID;

    protected String orderID;

    protected String symbol;

    protected double lastQty;

    protected double lastPx;

    protected double orderQty;

    //Valid options: Buy, Sell. 
    protected String side;

    // Valid options: Market, Limit, Stop, StopLimit, MarketIfTouched, LimitIfTouched, MarketWithLeftOverAsLimit, Pegged. 
    protected String ordType;

    protected String execType;

    protected String ordStatus;

    protected String ordRejectReason;

    protected double leavesQty;

    protected double execCost;

    protected double execCommision;

//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
//    protected ZonedDateTime transactTime;
//
//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
//    protected ZonedDateTime timestamp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(double orderQty) {
        this.orderQty = orderQty;
    }


    public String getOrdType() {
        return ordType;
    }

    public void setOrdType(String ordType) {
        this.ordType = ordType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(String ordStatus) {
        this.ordStatus = ordStatus;
    }

    public String getExecID() {
        return execID;
    }

    public void setExecID(String execID) {
        this.execID = execID;
    }

    public double getLastQty() {
        return lastQty;
    }

    public void setLastQty(double lastQty) {
        this.lastQty = lastQty;
    }

    public double getLastPx() {
        return lastPx;
    }

    public void setLastPx(double lastPx) {
        this.lastPx = lastPx;
    }

    public String getExecType() {
        return execType;
    }

    public void setExecType(String execType) {
        this.execType = execType;
    }

    public String getOrdRejectReason() {
        return ordRejectReason;
    }

    public void setOrdRejectReason(String ordRejectReason) {
        this.ordRejectReason = ordRejectReason;
    }

    public double getLeavesQty() {
        return leavesQty;
    }

    public void setLeavesQty(double leavesQty) {
        this.leavesQty = leavesQty;
    }

    public double getExecCost() {
        return execCost;
    }

    public void setExecCost(double execCost) {
        this.execCost = execCost;
    }

    public double getExecCommision() {
        return execCommision;
    }

    public void setExecCommision(double execCommision) {
        this.execCommision = execCommision;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.execID);
        hash = 41 * hash + Objects.hashCode(this.orderID);
        hash = 41 * hash + Objects.hashCode(this.symbol);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.lastQty) ^ (Double.doubleToLongBits(this.lastQty) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.lastPx) ^ (Double.doubleToLongBits(this.lastPx) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.orderQty) ^ (Double.doubleToLongBits(this.orderQty) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.side);
        hash = 41 * hash + Objects.hashCode(this.ordType);
        hash = 41 * hash + Objects.hashCode(this.execType);
        hash = 41 * hash + Objects.hashCode(this.ordStatus);
        hash = 41 * hash + Objects.hashCode(this.ordRejectReason);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.leavesQty) ^ (Double.doubleToLongBits(this.leavesQty) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.execCost) ^ (Double.doubleToLongBits(this.execCost) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.execCommision) ^ (Double.doubleToLongBits(this.execCommision) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BitmexExecution other = (BitmexExecution) obj;
        if (Double.doubleToLongBits(this.lastQty) != Double.doubleToLongBits(other.lastQty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lastPx) != Double.doubleToLongBits(other.lastPx)) {
            return false;
        }
        if (Double.doubleToLongBits(this.orderQty) != Double.doubleToLongBits(other.orderQty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.leavesQty) != Double.doubleToLongBits(other.leavesQty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.execCost) != Double.doubleToLongBits(other.execCost)) {
            return false;
        }
        if (Double.doubleToLongBits(this.execCommision) != Double.doubleToLongBits(other.execCommision)) {
            return false;
        }
        if (!Objects.equals(this.execID, other.execID)) {
            return false;
        }
        if (!Objects.equals(this.orderID, other.orderID)) {
            return false;
        }
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if (!Objects.equals(this.side, other.side)) {
            return false;
        }
        if (!Objects.equals(this.ordType, other.ordType)) {
            return false;
        }
        if (!Objects.equals(this.execType, other.execType)) {
            return false;
        }
        if (!Objects.equals(this.ordStatus, other.ordStatus)) {
            return false;
        }
        if (!Objects.equals(this.ordRejectReason, other.ordRejectReason)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexExecution{" + "execID=" + execID + ", orderID=" + orderID + ", symbol=" + symbol + ", lastQty=" + lastQty + ", lastPx=" + lastPx + ", orderQty=" + orderQty + ", side=" + side + ", ordType=" + ordType + ", execType=" + execType + ", ordStatus=" + ordStatus + ", ordRejectReason=" + ordRejectReason + ", leavesQty=" + leavesQty + ", execCost=" + execCost + ", execCommision=" + execCommision + '}';
    }

 
    

}
