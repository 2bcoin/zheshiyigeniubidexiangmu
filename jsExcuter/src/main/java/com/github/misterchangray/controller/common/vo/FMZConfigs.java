package com.github.misterchangray.controller.common.vo;

public class FMZConfigs {

    public static String collectionName = "FMZConfigs";

    private String id;
    private String title;
    private String AKEY;
    private String SKEY;
    private String ORG;
    private String COIN;
    private String TYPE;
    private String ContractType;
    private String Direction;
    private String MarginLevel;
    private String DefaultPeriod;
    private Long creatTime;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public static String getCollectionName() {
        return collectionName;
    }

    public static void setCollectionName(String collectionName) {
        FMZConfigs.collectionName = collectionName;
    }

    public String getAKEY() {
        return AKEY;
    }

    public void setAKEY(String AKEY) {
        this.AKEY = AKEY;
    }

    public String getSKEY() {
        return SKEY;
    }

    public void setSKEY(String SKEY) {
        this.SKEY = SKEY;
    }

    public String getORG() {
        return ORG;
    }

    public void setORG(String ORG) {
        this.ORG = ORG;
    }

    public String getCOIN() {
        return COIN;
    }

    public void setCOIN(String COIN) {
        this.COIN = COIN;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getContractType() {
        return ContractType;
    }

    public void setContractType(String contractType) {
        ContractType = contractType;
    }

    public String getDirection() {
        return Direction;
    }

    public void setDirection(String direction) {
        Direction = direction;
    }

    public String getMarginLevel() {
        return MarginLevel;
    }

    public void setMarginLevel(String marginLevel) {
        MarginLevel = marginLevel;
    }

    public String getDefaultPeriod() {
        return DefaultPeriod;
    }

    public void setDefaultPeriod(String defaultPeriod) {
        DefaultPeriod = defaultPeriod;
    }

    @Override
    public String toString() {
        return "FMZConfigs{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", AKEY='" + AKEY + '\'' +
                ", SKEY='" + SKEY + '\'' +
                ", ORG='" + ORG + '\'' +
                ", COIN='" + COIN + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", ContractType='" + ContractType + '\'' +
                ", Direction='" + Direction + '\'' +
                ", MarginLevel='" + MarginLevel + '\'' +
                ", DefaultPeriod='" + DefaultPeriod + '\'' +
                ", creatTime=" + creatTime +
                '}';
    }
}
