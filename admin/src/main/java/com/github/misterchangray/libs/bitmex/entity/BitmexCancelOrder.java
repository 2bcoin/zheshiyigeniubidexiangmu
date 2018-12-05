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
public class BitmexCancelOrder {
    
    protected String orderID;
    

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.orderID);
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
        final BitmexCancelOrder other = (BitmexCancelOrder) obj;
        if (!Objects.equals(this.orderID, other.orderID)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexCancelOrder{" + "orderID=" + orderID + '}';
    }

    

    
}