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
public class BitmexPosition {
    
    protected String symbol;
    protected String currency;
    protected String underlying;
    protected String quoteCurrency;
    protected double commission;
    protected double currentQty;
    protected double currentCost;
    protected double currentComm;
    protected boolean isOpen;
    protected double simpleQty;
    protected double simpleCost;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(double currentQty) {
        this.currentQty = currentQty;
    }

    public double getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(double currentCost) {
        this.currentCost = currentCost;
    }

    public double getCurrentComm() {
        return currentComm;
    }

    public void setCurrentComm(double currentComm) {
        this.currentComm = currentComm;
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public double getSimpleQty() {
        return simpleQty;
    }

    public void setSimpleQty(double simpleQty) {
        this.simpleQty = simpleQty;
    }

    public double getSimpleCost() {
        return simpleCost;
    }

    public void setSimpleCost(double simpleCost) {
        this.simpleCost = simpleCost;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.symbol);
        hash = 19 * hash + Objects.hashCode(this.currency);
        hash = 19 * hash + Objects.hashCode(this.underlying);
        hash = 19 * hash + Objects.hashCode(this.quoteCurrency);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.commission) ^ (Double.doubleToLongBits(this.commission) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.currentQty) ^ (Double.doubleToLongBits(this.currentQty) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.currentCost) ^ (Double.doubleToLongBits(this.currentCost) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.currentComm) ^ (Double.doubleToLongBits(this.currentComm) >>> 32));
        hash = 19 * hash + (this.isOpen ? 1 : 0);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.simpleQty) ^ (Double.doubleToLongBits(this.simpleQty) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.simpleCost) ^ (Double.doubleToLongBits(this.simpleCost) >>> 32));
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
        final BitmexPosition other = (BitmexPosition) obj;
        if (Double.doubleToLongBits(this.commission) != Double.doubleToLongBits(other.commission)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentQty) != Double.doubleToLongBits(other.currentQty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentCost) != Double.doubleToLongBits(other.currentCost)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentComm) != Double.doubleToLongBits(other.currentComm)) {
            return false;
        }
        if (this.isOpen != other.isOpen) {
            return false;
        }
        if (Double.doubleToLongBits(this.simpleQty) != Double.doubleToLongBits(other.simpleQty)) {
            return false;
        }
        if (Double.doubleToLongBits(this.simpleCost) != Double.doubleToLongBits(other.simpleCost)) {
            return false;
        }
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if (!Objects.equals(this.currency, other.currency)) {
            return false;
        }
        if (!Objects.equals(this.underlying, other.underlying)) {
            return false;
        }
        if (!Objects.equals(this.quoteCurrency, other.quoteCurrency)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexPosition{" + "symbol=" + symbol + ", currency=" + currency + ", underlying=" + underlying + ", quoteCurrency=" + quoteCurrency + ", commission=" + commission + ", currentQty=" + currentQty + ", currentCost=" + currentCost + ", currentComm=" + currentComm + ", isOpen=" + isOpen + ", simpleQty=" + simpleQty + ", simpleCost=" + simpleCost + '}';
    }
            
}
