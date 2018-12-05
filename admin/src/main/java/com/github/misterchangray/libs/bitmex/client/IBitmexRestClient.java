/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.client;

import com.github.misterchangray.libs.bitmex.entity.BitmexAmendOrder;
import com.github.misterchangray.libs.bitmex.entity.BitmexChartData;
import com.github.misterchangray.libs.bitmex.entity.BitmexInstrument;
import com.github.misterchangray.libs.bitmex.entity.BitmexOrder;
import com.github.misterchangray.libs.bitmex.data.Ticker;

import java.util.List;

/**
 *
 * @author RobTerpilowski
 */
public interface IBitmexRestClient {

    BitmexOrder amendOrder(BitmexAmendOrder order);

    BitmexOrder[] cancelOrder(BitmexOrder order);

    List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize);

    List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize, String endTime);
    
    List<BitmexChartData> getChartData(Ticker ticker, int count, BitmexRestClient.ChartDataBinSize binSize, String endTime, boolean getInprogressBar);

    BitmexInstrument getInstrument(Ticker ticker);

    BitmexOrder submitOrder(BitmexOrder order);
    
}
