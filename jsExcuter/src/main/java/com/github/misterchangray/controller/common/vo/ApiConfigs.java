package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.controller.common.OrgsInfoEnum;

public class ApiConfigs {
    public static String collectionName = "apiConfigs";

    private String id;
    private String account;
    private OrgsInfoEnum org;
    private String desc;
    private String aKey;
    private String sKey;
    private long createTime;


    public ApiConfigs(){}
    public ApiConfigs(OrgsInfoEnum org, String aKey, String sKey) {
        this.org = org;
        this.aKey = aKey;
        this.sKey = sKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public OrgsInfoEnum getOrg() {
        return org;
    }

    public void setOrg(OrgsInfoEnum org) {
        this.org = org;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getaKey() {
        return aKey;
    }

    public void setaKey(String aKey) {
        this.aKey = aKey;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }
}
