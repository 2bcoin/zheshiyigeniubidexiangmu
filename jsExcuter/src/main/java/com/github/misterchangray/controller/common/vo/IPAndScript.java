package com.github.misterchangray.controller.common.vo;

import java.util.List;

public class IPAndScript {

    public static String collectionName = "IPAndScript";

    private String id;
    private String ip;
    private String title;// 名称
    private String scriptID; //脚本ID
    private String scriptName;//脚本名称
    private String str; //拼接字符串 查重
    private List<String> configurationName;//配置名字
    private List<String> configurationID;//配置ID
    private String customConfig;//自定义配置
    private String robotStatus;//查看是否启动 true启动,flase关闭
    private Long creatTime;//创建时间
    private Long updateTime;//修改时间
    private Long starTime;//启动时间

    public Long getStarTime() {
        return starTime;
    }

    public void setStarTime(Long starTime) {
        this.starTime = starTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(String customConfig) {
        this.customConfig = customConfig;
    }

    public List<String> getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(List<String> configurationName) {
        this.configurationName = configurationName;
    }

    public List<String> getConfigurationID() {
        return configurationID;
    }

    public void setConfigurationID(List<String> configurationID) {
        this.configurationID = configurationID;
    }

    public String getRobotStatus() {
        return robotStatus;
    }

    public void setRobotStatus(String robotStatus) {
        this.robotStatus = robotStatus;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
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
        IPAndScript.collectionName = collectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScriptID() {
        return scriptID;
    }

    public void setScriptID(String scriptID) {
        this.scriptID = scriptID;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "IPAndScript{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", title='" + title + '\'' +
                ", scriptID='" + scriptID + '\'' +
                ", scriptName='" + scriptName + '\'' +
                ", str='" + str + '\'' +
                ", configurationName=" + configurationName +
                ", configurationID=" + configurationID +
                ", customConfig='" + customConfig + '\'' +
                ", robotStatus='" + robotStatus + '\'' +
                ", creatTime=" + creatTime +
                ", updateTime=" + updateTime +
                ", starTime=" + starTime +
                '}';
    }
}
