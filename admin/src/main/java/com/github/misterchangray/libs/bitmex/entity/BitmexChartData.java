/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author RobTerpilowski
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitmexChartData {
    
    protected ZonedDateTime timestamp;
    protected String symbol;
    protected double open;
    protected double high;
    protected double low;
    protected double close;
    protected int trades;
    protected double volume;
    protected double vwap;

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public int getTrades() {
        return trades;
    }

    public void setTrades(int trades) {
        this.trades = trades;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.timestamp);
        hash = 79 * hash + Objects.hashCode(this.symbol);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.open) ^ (Double.doubleToLongBits(this.open) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.high) ^ (Double.doubleToLongBits(this.high) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.low) ^ (Double.doubleToLongBits(this.low) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.close) ^ (Double.doubleToLongBits(this.close) >>> 32));
        hash = 79 * hash + this.trades;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.volume) ^ (Double.doubleToLongBits(this.volume) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.vwap) ^ (Double.doubleToLongBits(this.vwap) >>> 32));
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
        final BitmexChartData other = (BitmexChartData) obj;
        if (Double.doubleToLongBits(this.open) != Double.doubleToLongBits(other.open)) {
            return false;
        }
        if (Double.doubleToLongBits(this.high) != Double.doubleToLongBits(other.high)) {
            return false;
        }
        if (Double.doubleToLongBits(this.low) != Double.doubleToLongBits(other.low)) {
            return false;
        }
        if (Double.doubleToLongBits(this.close) != Double.doubleToLongBits(other.close)) {
            return false;
        }
        if (this.trades != other.trades) {
            return false;
        }
        if (Double.doubleToLongBits(this.volume) != Double.doubleToLongBits(other.volume)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vwap) != Double.doubleToLongBits(other.vwap)) {
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
        return "BitmaxChartData{" + "timestamp=" + timestamp + ", symbol=" + symbol + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", trades=" + trades + ", volume=" + volume + ", vwap=" + vwap + '}';
    }
    
    
}
