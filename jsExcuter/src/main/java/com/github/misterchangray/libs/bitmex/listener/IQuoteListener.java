/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.misterchangray.libs.bitmex.listener;

import com.github.misterchangray.libs.bitmex.entity.BitmexQuote;
import com.google.gson.JsonObject;

/**
 *
 * @author RobTerpilowski
 */
public interface IQuoteListener {
    
    public void quoteUpdated(BitmexQuote quoteData);
}
