package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.controller.common.OrgsInfoEnum;

public class TAConfig {
    @Override
    public String toString() {
        return "TAConfig{" +
                "org=" + org +
                ", coin='" + coin + '\'' +
                ", amountPrecision=" + amountPrecision +
                ", pricePrecision=" + pricePrecision +
                ", minAmount=" + minAmount +
                '}';
    }

    private OrgsInfoEnum org;
    private String coin;
    private int amountPrecision;//数量精度
    private int pricePrecision;//价格精度
    private Double minAmount; //最小下单数量

    /**
     * 默认费率
     * @param org
     * @param coin
     * @param price
     * @param amount
     */
    public TAConfig(OrgsInfoEnum org, String coin, int price, int amount, double minAmount) {
        this.org = org;
        this.coin = coin;
        this.amountPrecision = amount;
        this.pricePrecision = price;
        this.minAmount = minAmount;
    }


    public TAConfig() {}


    public OrgsInfoEnum getOrg() {
        return org;
    }

    public void setOrg(OrgsInfoEnum org) {
        this.org = org;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public int getAmountPrecision() {
        return amountPrecision;
    }

    public void setAmountPrecision(int amountPrecision) {
        this.amountPrecision = amountPrecision;
    }

    public int getPricePrecision() {
        return pricePrecision;
    }

    public void setPricePrecision(int pricePrecision) {
        this.pricePrecision = pricePrecision;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }
}
