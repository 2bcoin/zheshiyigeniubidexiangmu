package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 * "name": "btcusdt",
 "base_currency": "btc",
 "quote_currency": "usdt",
 "price_decimal": 2,
 "amount_decimal": 4
 */
public class FcoinSymbio {
    private String name;
    private String base_currency;
    private String quote_currency;
    private Integer price_decimal;
    private Integer amount_decimal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase_currency() {
        return base_currency;
    }

    public void setBase_currency(String base_currency) {
        this.base_currency = base_currency;
    }

    public String getQuote_currency() {
        return quote_currency;
    }

    public void setQuote_currency(String quote_currency) {
        this.quote_currency = quote_currency;
    }

    public Integer getPrice_decimal() {
        return price_decimal;
    }

    public void setPrice_decimal(Integer price_decimal) {
        this.price_decimal = price_decimal;
    }

    public Integer getAmount_decimal() {
        return amount_decimal;
    }

    public void setAmount_decimal(Integer amount_decimal) {
        this.amount_decimal = amount_decimal;
    }
}
