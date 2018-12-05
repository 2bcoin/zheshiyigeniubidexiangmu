package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.common.enums.CoinOtc;

/**
 * 电话记录
 */
public class PhoneCallRecord {
    public static String collectionName = "PhoneCallRecord";
    /**
     * ID
     */
    private String id;
    /**
     * 币种
     */
    private CoinOtc coinOtc;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 创建时间
     */
    private Long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CoinOtc getCoinOtc() {
        return coinOtc;
    }

    public void setCoinOtc(CoinOtc coinOtc) {
        this.coinOtc = coinOtc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PhoneCallRecord{" +
                "id='" + id + '\'' +
                ", coinOtc=" + coinOtc +
                ", phone='" + phone + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
