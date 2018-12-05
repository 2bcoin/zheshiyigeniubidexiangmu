package com.github.misterchangray.service.jsexcutor.dto;

import java.util.List;

public class ScriptDto {
    private String scriptId; //脚本ID
    private String script; //脚本
    private String config; // 发明者配置
    private List<String> configId; // 发明者配置Id
    private String title; //策略名称
    private String tag; //脚本分类
    private String ipAndScriptId; //托管者ID
    private String customConfig;//自定义配置
    private String ipAndScriptName;


    public String getCustomConfig() {
        return customConfig;
    }

    public void setCustomConfig(String customConfig) {
        this.customConfig = customConfig;
    }

    public String getIpAndScriptName() {
        return ipAndScriptName;
    }

    public void setIpAndScriptName(String ipAndScriptName) {
        this.ipAndScriptName = ipAndScriptName;
    }

    private Long date = System.currentTimeMillis();

    public void setDate(Long date) {
        this.date = date;
    }

    public List<String> getConfigId() {
        return configId;
    }

    public void setConfigId(List<String> configId) {
        this.configId = configId;
    }

    public Long getDate() {
        return date;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getIpAndScriptId() {
        return ipAndScriptId;
    }

    public void setIpAndScriptId(String ipAndScriptId) {
        this.ipAndScriptId = ipAndScriptId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ScriptDto{" +
                "scriptId='" + scriptId + '\'' +
                ", script='" + script + '\'' +
                ", config='" + config + '\'' +
                ", configId='" + configId + '\'' +
                ", title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", ipAndScriptId='" + ipAndScriptId + '\'' +
                ", date=" + date +
                '}';
    }
}
