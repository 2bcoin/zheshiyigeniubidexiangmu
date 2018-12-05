package com.github.misterchangray.service.po;


/**
 * 此类用于储存业务日志
 */
public class DbLog {
    public static String collectionName = "dbLogs";

    private long time;
    private String date;
    private String log;
    private String type;//日志分类

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
