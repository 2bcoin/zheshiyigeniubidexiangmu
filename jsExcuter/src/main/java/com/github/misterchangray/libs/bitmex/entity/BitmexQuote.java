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
public class BitmexQuote {
    
    protected String symbol;
    protected double bidPrice;
    protected double askPrice;
    protected int bidSize;
    protected int askSize;
    protected String timestamp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getBidSize() {
        return bidSize;
    }

    public void setBidSize(int bidSize) {
        this.bidSize = bidSize;
    }

    public int getAskSize() {
        return askSize;
    }

    public void setAskSize(int askSize) {
        this.askSize = askSize;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.symbol);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.bidPrice) ^ (Double.doubleToLongBits(this.bidPrice) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.askPrice) ^ (Double.doubleToLongBits(this.askPrice) >>> 32));
        hash = 37 * hash + this.bidSize;
        hash = 37 * hash + this.askSize;
        hash = 37 * hash + Objects.hashCode(this.timestamp);
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
        final BitmexQuote other = (BitmexQuote) obj;
        if (Double.doubleToLongBits(this.bidPrice) != Double.doubleToLongBits(other.bidPrice)) {
            return false;
        }
        if (Double.doubleToLongBits(this.askPrice) != Double.doubleToLongBits(other.askPrice)) {
            return false;
        }
        if (this.bidSize != other.bidSize) {
            return false;
        }
        if (this.askSize != other.askSize) {
            return false;
        }
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexQuote{" + "symbol=" + symbol + ", bidPrice=" + bidPrice + ", askPrice=" + askPrice + ", bidSize=" + bidSize + ", askSize=" + askSize + ", timestamp=" + timestamp + '}';
    }
    
}
