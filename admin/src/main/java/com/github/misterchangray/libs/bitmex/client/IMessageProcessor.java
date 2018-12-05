/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;


import com.github.misterchangray.libs.bitmex.listener.*;

/**
 *
 * @author RobTerpilowski
 */
public interface IMessageProcessor {

    void addPositionListener(IPositionListener listener);

    void addQuoteListener(IQuoteListener listener);

    void addOrderListener(IOrderListener listener);
    
    void addTradeListener(ITradeListener listener);
    
    void addExecutionListener(IExecutionListener listener);

    void processMessage(String message);

    void startProcessor();

    void stopProcessor();
    
    int getQueueSize();
    
    void addPongListener(IPongListener listener);
    
}
