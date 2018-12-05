/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;


import com.github.misterchangray.libs.bitmex.entity.BitmexOrder;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexSystemOverloadedException extends RuntimeException {

    protected BitmexOrder submittedOrder;
    
    public BitmexSystemOverloadedException(BitmexOrder submittedOrder ) {
        super("The system is currently overloaded. Please try again later");
        this.submittedOrder = submittedOrder;
    }

    public BitmexOrder getSubmittedOrder() {
        return submittedOrder;
    }
    
    
    
    
    
    
}
