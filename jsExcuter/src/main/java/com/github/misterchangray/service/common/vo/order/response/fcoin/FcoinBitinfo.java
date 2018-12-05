package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 */
public class FcoinBitinfo {
    private String currency;
    private String available;
    private String frozen;
    private String balance;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
