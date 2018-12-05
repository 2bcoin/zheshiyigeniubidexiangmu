package com.github.misterchangray.controller.common.vo;

import com.github.misterchangray.common.enums.CoinOtc;

/**
 * 监控配置
 */
public class MonitorConfig {
    public static String collectionName = "MonitorConfig";
    /**
     * ID
     */
    private String id;
    /**
     * 币种
     */
    private CoinOtc coinOtc;
    /**
     * 合约类型
     */
    private String contractType;
    /**
     * 周期
     */
    private String cycle;
    /**
     * 时长
     */
    private Long size;
    /**
     * 价格波动阀值 (最高价-最低价)/当前价
     */
    private Double priceSurgeThreshold;
    /**
     * 最大交易量比率 最大交易量/分钟平均交易量
     */
    private Double maxVolumeRateThreshold;
    /**
     * 第二大交易量变化率 分钟最大交易量/分钟第二大交易量
     */
    private Double maxSecondVolumeVaryThreshold;
    /**
     * 是否删除
     */
    private Boolean deleted;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

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

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Double getPriceSurgeThreshold() {
        return priceSurgeThreshold;
    }

    public void setPriceSurgeThreshold(Double priceSurgeThreshold) {
        this.priceSurgeThreshold = priceSurgeThreshold;
    }

    public Double getMaxVolumeRateThreshold() {
        return maxVolumeRateThreshold;
    }

    public void setMaxVolumeRateThreshold(Double maxVolumeRateThreshold) {
        this.maxVolumeRateThreshold = maxVolumeRateThreshold;
    }

    public Double getMaxSecondVolumeVaryThreshold() {
        return maxSecondVolumeVaryThreshold;
    }

    public void setMaxSecondVolumeVaryThreshold(Double maxSecondVolumeVaryThreshold) {
        this.maxSecondVolumeVaryThreshold = maxSecondVolumeVaryThreshold;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MonitorConfig{" +
                "id='" + id + '\'' +
                ", coinOtc=" + coinOtc +
                ", contractType='" + contractType + '\'' +
                ", cycle='" + cycle + '\'' +
                ", size=" + size +
                ", priceSurgeThreshold=" + priceSurgeThreshold +
                ", maxVolumeRateThreshold=" + maxVolumeRateThreshold +
                ", maxSecondVolumeVaryThreshold=" + maxSecondVolumeVaryThreshold +
                ", deleted=" + deleted +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
