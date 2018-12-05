package com.github.misterchangray.service.po;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 保存账户各个平台余额情况;数据结构
 * account
 */
public class AccountBalance {
    public static String collectionName = "accountInfo";
    public static String spot = "spot"; //现货
    public static String otc = "otc";//期货

    private JsonNode info;	//交易所返回的原始结构
    private BigDecimal	balance;	//	余额(定价货币余额, ETH_BTC的话BTC为定价货币)
    private BigDecimal	frozenBalance;	//	冻结的余额
    private BigDecimal	stocks;	//	交易货币的可用数量, 数字货币现货为当前可操作币的余额(去掉冻结的币), 数字货币期货的话为合约当前可用保证金(传统期货无此属性) 。
    private BigDecimal frozenStocks;	//	冻结的交易货币的可用数量(传统期货无此属性)
    private BigDecimal balance_usd; //把所有持有货币折算成usd得价格
    private BigDecimal balance_btc; //把所有持有货币折算成btc得价格
    private String type; //otc , spot
    private String aKey;//apikey
    private String account;//账号
    private Boolean last;//是否为最新数据
    private String orgName;
    private Long time = System.currentTimeMillis();//抓取时间

    public BigDecimal getBalance_usd() {
        return balance_usd;
    }

    public void setBalance_usd(BigDecimal balance_usd) {
        this.balance_usd = balance_usd;
    }

    public BigDecimal getBalance_btc() {
        return balance_btc;
    }

    public void setBalance_btc(BigDecimal balance_btc) {
        this.balance_btc = balance_btc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public JsonNode getInfo() {
        return info;
    }

    public void setInfo(JsonNode info) {
        this.info = info;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public BigDecimal getStocks() {
        return stocks;
    }

    public void setStocks(BigDecimal stocks) {
        this.stocks = stocks;
    }

    public BigDecimal getFrozenStocks() {
        return frozenStocks;
    }

    public void setFrozenStocks(BigDecimal frozenStocks) {
        this.frozenStocks = frozenStocks;
    }

    public String getaKey() {
        return aKey;
    }

    public void setaKey(String aKey) {
        this.aKey = aKey;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
