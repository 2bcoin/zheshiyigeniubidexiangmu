package com.github.misterchangray.service.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 保存账户各个平台余额情况;数据结构
 * account
 */
public class AccountInfo {
    public static String collectionName = "accountInfo";

    public static String locked_balance = "locked_balance"; //冻结余额
    public static String balance = "balance";//可用余额
    public static String trading = "trading"; //交易中

    public static String spot = "spot"; //现货
    public static String otc = "otc";//期货

    private String aKey;//apikey
    private String account;//账号

    private Boolean last;//是否为最新数据
    private String orgName;
    //储存每个币种得余额;

    public Map<String, Object> getBalanceInfo() {
        return balanceInfo;
    }

    public void setBalanceInfo(Map<String, Object> balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    /**
     * {
     *     btc: {
     *         locked_balance:0, //冻结余额
     *         balance : 0, //可用余额
     *         trading:"json string" //交易中
     *     }
     * }
     */
    private Map<String, Object> balanceInfo;

    //现货还是期货
    private String type;
    //插入时间
    private Long time;
    //原始数据;指解析前得json数据
    private String rawData;

    public String getRawData() {
        return rawData;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public AccountInfo() {
        this.balanceInfo = new HashMap<>();
        this.last = true;
    }

    public String getaKey() {
        return aKey;
    }

    public void setaKey(String aKey) {
        this.aKey = aKey;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
