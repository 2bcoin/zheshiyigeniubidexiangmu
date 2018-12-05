/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.libs.bitmex.entity.*;
import com.github.misterchangray.libs.bitmex.listener.*;
import com.github.misterchangray.libs.bitmex.data.Ticker;

import java.util.List;

/**
 *
 * @author RobTerpilowski
 */
public class BitmexClient implements IBitmexClient {

    protected IBitmexRestClient restClient;
    protected IBitmexWebsocketClient websocketClient;
    
    
    public BitmexClient( boolean useProduction ) {
        restClient = new BitmexRestClient(useProduction);
        websocketClient = new BitmexWebsocketClient(useProduction);
        websocketClient.connect();
    }
    
    public BitmexClient( boolean useProduction, String apiKeyName, String apiKey ) {
        restClient = new BitmexRestClient(useProduction, apiKeyName, apiKey);
        websocketClient = new BitmexWebsocketClient(useProduction);
        websocketClient.connect(apiKeyName, apiKey);
    }
    
    
    @Override
    public BitmexOrder amendOrder(BitmexAmendOrder order) {
        return restClient.amendOrder(order);
    }

    @Override
    public BitmexOrder[] cancelOrder(BitmexOrder order) {
        return restClient.cancelOrder(order);
    }

    @Override
    public List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize) {
        return restClient.getChartData(ticker, count, binSize);
    }

    @Override
    public List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize, String endTime) {
        return restClient.getChartData(ticker, count, binSize, endTime);
    }

    @Override
    public List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize, String endTime, boolean getInprogressBar) {
        return restClient.getChartData(ticker, count, binSize, endTime, getInprogressBar);
    }

    
    
    @Override
    public BitmexInstrument getInstrument(Ticker ticker) {
        return restClient.getInstrument(ticker);
    }

    @Override
    public BitmexOrder submitOrder(BitmexOrder order) {
        return restClient.submitOrder(order);
    }

    @Override
    public boolean connect() {
        return websocketClient.connect();
    }

    @Override
    public boolean connect(String apiKey, String apiSecret) {
        return websocketClient.connect(apiKey, apiSecret);
    }

    @Override
    public void disconnect() {
        websocketClient.disconnect();
    }

    @Override
    public String getApiSignature(String apiKey, long nonce) {
        return websocketClient.getApiSignature(apiKey, nonce);
    }

    @Override
    public boolean isConnected() {
        return websocketClient.isConnected();
    }

    @Override
    public void quoteUpdated(BitmexQuote quoteData) {
        
    }

    @Override
    public void subscribeExecutions(IExecutionListener listener) {
        websocketClient.subscribeExecutions(listener);
    }

    @Override
    public void subscribeFunding(Ticker ticker) {
        websocketClient.subscribeFunding(ticker);
    }

    @Override
    public void subscribeInstrument(Ticker ticker) {
        websocketClient.subscribeInstrument(ticker);
    }

    @Override
    public void subscribeOrderBook(Ticker ticker) {
        websocketClient.subscribeOrderBook(ticker);
    }

    @Override
    public void subscribeOrders(IOrderListener listener) {
        websocketClient.subscribeOrders(listener);
    }

    @Override
    public void subscribePositions(IPositionListener listener) {
        websocketClient.subscribePositions(listener);
    }

    @Override
    public void subscribeQuotes(Ticker ticker, IQuoteListener listener) {
        websocketClient.subscribeQuotes(ticker, listener);
    }

    @Override
    public void subscribeTrades(Ticker ticker, ITradeListener listener) {
        websocketClient.subscribeTrades(ticker, listener);
    }
    
    
    
    
}
