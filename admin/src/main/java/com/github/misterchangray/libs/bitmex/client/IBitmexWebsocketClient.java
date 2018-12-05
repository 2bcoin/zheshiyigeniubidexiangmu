/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;


import com.github.misterchangray.libs.bitmex.data.Ticker;
import com.github.misterchangray.libs.bitmex.entity.BitmexQuote;
import com.github.misterchangray.libs.bitmex.listener.*;

/**
 *
 * @author RobTerpilowski
 */
public interface IBitmexWebsocketClient extends IQuoteListener {

    boolean connect();

    boolean connect(String apiKey, String apiSecret);

    void disconnect();

    String getApiSignature(String apiKey, long nonce);

    boolean isConnected();

    void quoteUpdated(BitmexQuote quoteData);

    void subscribeExecutions(IExecutionListener listener);

    void subscribeFunding(Ticker ticker);

    void subscribeInstrument(Ticker ticker);

    void subscribeOrderBook(Ticker ticker);

    void subscribeOrders(IOrderListener listener);

    void subscribePositions(IPositionListener listener);

    void subscribeQuotes(Ticker ticker, IQuoteListener listener);

    void subscribeTrades(Ticker ticker, ITradeListener listener);
    
}
