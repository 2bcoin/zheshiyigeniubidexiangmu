package com.github.misterchangray.service.platform.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public class Position {
    private JsonNode info;//交易所返回的原始结构
    private BigDecimal marginLevel;//杆杠大小, OKCoin为10或者20,OK期货的全仓模式返回为固定的10, 因为原生API不支持
    private BigDecimal amount;//持仓量,OKCoin表示合约的份数(整数且大于1)
    private BigDecimal canCover;//可平量, 只有股票有此选项, 表示可以平仓的数量(股票为T+1)今日仓不能平
    private BigDecimal frozenAmount;//仓位冻结量
    private BigDecimal price;//持仓均价
    private BigDecimal profit;//商品期货：持仓盯市盈亏，数字货币：(数字货币单位：BTC/LTC, 传统期货单位:RMB, 股票不支持此字段, 注: OKCoin期货全仓情况下指实现盈余, 并非持仓盈亏, 逐仓下指持仓盈亏)
    private Integer type;//0PD_LONG为多头仓位(CTP中用closebuy_today平仓), 1PD_SHORT为空头仓位(CTP用closesell_today)平仓, (CTP期货中)PD_LONG_YD为昨日多头仓位(用closebuy平), PD_SHORT_YD为昨日空头仓位(用closesell平)
    private String contractType;//商品期货为合约代码, 股票为’交易所代码_股票代码’, 具体参数SetContractType的传入类型

    public JsonNode getInfo() {
        return info;
    }

    public void setInfo(JsonNode info) {
        this.info = info;
    }

    public BigDecimal getMarginLevel() {
        return marginLevel;
    }

    public void setMarginLevel(BigDecimal marginLevel) {
        this.marginLevel = marginLevel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCanCover() {
        return canCover;
    }

    public void setCanCover(BigDecimal canCover) {
        this.canCover = canCover;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}
