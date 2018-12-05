package com.github.misterchangray.controller.common.vo;

public class ScriptData {

    public static String collectionName = "scriptData";

    private String id;
    private String title;
    private String scriptStr;
    private String tag;
    private Long creatTime;//创建时间
    private Long updateTime;//修改时间

    public static String getCollectionName() {
        return collectionName;
    }

    public static void setCollectionName(String collectionName) {
        ScriptData.collectionName = collectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScriptStr() {
        return scriptStr;
    }

    public void setScriptStr(String scriptStr) {
        this.scriptStr = scriptStr;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Long creatTime) {
        this.creatTime = creatTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
