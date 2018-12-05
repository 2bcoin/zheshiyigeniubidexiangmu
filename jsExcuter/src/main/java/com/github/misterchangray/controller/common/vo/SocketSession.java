package com.github.misterchangray.controller.common.vo;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class SocketSession {
    private String subscribe;
    private Session session;
    private long createTime;
    private Long lastPushTime; //最后一次推送时间;第一位为null

    public SocketSession(String subscribe, Session session) {
        this.subscribe = subscribe;
        this.session = session;
        this.createTime = System.currentTimeMillis();
        this.lastPushTime = null;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }



    public Long getLastPushTime() {
        return lastPushTime;
    }

    public void setLastPushTime(Long lastPushTime) {
        this.lastPushTime = lastPushTime;
    }
}
