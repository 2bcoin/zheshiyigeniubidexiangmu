package com.github.misterchangray.controller.vo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 物理机器
 */
public class Slave {
    private String ip;
    CopyOnWriteArrayList<RobotInfo> robotInfoList;
    private int retryCount;


    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public static Slave build(String ip) {
        Slave slave = new Slave();
        slave.setIp(ip);
        slave.setRobotInfoList(new CopyOnWriteArrayList<>());
        return slave;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<RobotInfo> getRobotInfoList() {
        return robotInfoList;
    }

    public void setRobotInfoList(CopyOnWriteArrayList<RobotInfo> robotInfoList) {
        this.robotInfoList = robotInfoList;
    }
}
