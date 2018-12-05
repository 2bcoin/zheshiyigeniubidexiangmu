/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.misterchangray.libs.bitmex.client.ZonedDateTimeDeserializer;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 *
 * @author RobTerpilowski
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitmexInstrument {
    
    protected String symbol;
    protected double indicativeFundingRate;
    protected double fundingRate;
    
    
    @JsonDeserialize( using = ZonedDateTimeDeserializer.class)
    protected ZonedDateTime fundingTimestamp;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getIndicativeFundingRate() {
        return indicativeFundingRate;
    }

    public void setIndicativeFundingRate(double indicativeFundingRate) {
        this.indicativeFundingRate = indicativeFundingRate;
    }

    public double getFundingRate() {
        return fundingRate;
    }

    public void setFundingRate(double fundingRate) {
        this.fundingRate = fundingRate;
    }
    
    
    public double getAnnualizedFundingRate() {
        return fundingRate * 100.0 * 3 * 365;
    }
    
    public double getAnnualizedIndicativeFundingRate() {
        return indicativeFundingRate * 100.0 * 3 * 365;
    }

    public ZonedDateTime getFundingTimestamp() {
        return fundingTimestamp;
    }

    public void setFundingTimestamp(ZonedDateTime fundingTimestamp) {
        this.fundingTimestamp = fundingTimestamp;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.symbol);
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.indicativeFundingRate) ^ (Double.doubleToLongBits(this.indicativeFundingRate) >>> 32));
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.fundingRate) ^ (Double.doubleToLongBits(this.fundingRate) >>> 32));
        hash = 29 * hash + Objects.hashCode(this.fundingTimestamp);
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
        final BitmexInstrument other = (BitmexInstrument) obj;
        if (Double.doubleToLongBits(this.indicativeFundingRate) != Double.doubleToLongBits(other.indicativeFundingRate)) {
            return false;
        }
        if (Double.doubleToLongBits(this.fundingRate) != Double.doubleToLongBits(other.fundingRate)) {
            return false;
        }
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if (!Objects.equals(this.fundingTimestamp, other.fundingTimestamp)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexInstrument{" + "symbol=" + symbol + ", indicativeFundingRate=" + indicativeFundingRate + ", fundingRate=" + fundingRate + ", fundingTimestamp=" + fundingTimestamp + '}';
    }

    
}
