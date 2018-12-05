package com.github.misterchangray.service.jsexcutor.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.misterchangray.service.jsexcutor.FMZAdaptor;

import java.io.Serializable;

public class RunningImage implements Serializable  {
    @JsonIgnore
    private Thread thread;
    private Long tId;
    private ScriptDto scriptDto;
    @JsonIgnore
    private FMZAdaptor fmzAdaptor;
    private String scriptId;
    private String title;
    private boolean running;


    public RunningImage() { }

    public RunningImage(String tag, Thread thread, Long tId, ScriptDto script, FMZAdaptor fmzAdaptor) {
        this.title = tag;
        this.thread = thread;
        this.tId = tId;
        this.scriptDto = script;
        this.fmzAdaptor = fmzAdaptor;
        this.running = true;
        this.scriptId = script.getScriptId();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public FMZAdaptor getFmzAdaptor() {
        return fmzAdaptor;
    }

    public void setFmzAdaptor(FMZAdaptor fmzAdaptor) {
        this.fmzAdaptor = fmzAdaptor;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Long gettId() {
        return tId;
    }

    public void settId(Long tId) {
        this.tId = tId;
    }

    public ScriptDto getScript() {
        return scriptDto;
    }

    public void setScript(ScriptDto script) {
        this.scriptDto = script;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }
}
