/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.entity;

import java.util.Objects;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexError {
    
    protected BitmexErrorError error;

    public BitmexErrorError getError() {
        return error;
    }

    public void setError(BitmexErrorError error) {
        this.error = error;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.error);
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
        final BitmexError other = (BitmexError) obj;
        if (!Objects.equals(this.error, other.error)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BitmexErrorError{" + "error=" + error + '}';
    }
    
    
    
}
