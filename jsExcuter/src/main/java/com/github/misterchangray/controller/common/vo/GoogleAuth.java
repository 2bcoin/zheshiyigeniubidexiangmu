package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.controller.common.OrgsInfoEnum;

public class GoogleAuth {
    public static String collectionName = "googleAuths";
    private String id;
    private String account;
    private String desc;
    private String miyao; //密钥
    private long createTime;

    public static String getCollectionName() {
        return collectionName;
    }

    public static void setCollectionName(String collectionName) {
        GoogleAuth.collectionName = collectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMiyao() {
        return miyao;
    }

    public void setMiyao(String miyao) {
        this.miyao = miyao;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
