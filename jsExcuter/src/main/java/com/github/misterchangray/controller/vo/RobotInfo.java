package com.github.misterchangray.controller.vo;


/**
 * 托管者
 */
public class RobotInfo {
    private String id;
    private String ipAndScriptName;//托管者名字
    private String tid;
    private String scriptId;
    private String config;
    private long date =System.currentTimeMillis();

    public String getIpAndScriptName() {
        return ipAndScriptName;
    }

    public void setIpAndScriptName(String ipAndScriptName) {
        this.ipAndScriptName = ipAndScriptName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public long getDate() {
        return date;
    }


}
