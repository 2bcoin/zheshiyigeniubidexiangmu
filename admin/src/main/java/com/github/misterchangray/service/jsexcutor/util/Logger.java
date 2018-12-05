package com.github.misterchangray.service.jsexcutor.util;


import com.github.misterchangray.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器人日志
 */
public class Logger implements Serializable {
    public Long date; //日志时间
    public Level level; //日志级别
    public String tag; //日志分类
    public String message; //日志信息
    public String platform;//平台
    public String scriptId; //策略代号
    public String dateStr;

    public enum Level {
        Info, Warn, Error, Reboot, Buy, Sell, CancelOrder, Profit;
    };


    public static Level getLevelBuyName(String n) {
        for(Level a : Level.values()) {
            if(a.toString().equalsIgnoreCase(n)) return a;
        }
        return Level.Info;
    }
    public Logger(Level level, String tag, String message, String platform, String scriptId) {
        this.date = System.currentTimeMillis();
        this.dateStr = DateUtils.dateToStr(new Date());
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.platform = platform;
        this.scriptId = scriptId;
    }

    public Logger() {
    }
}
