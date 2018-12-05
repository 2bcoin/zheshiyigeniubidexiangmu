package com.github.misterchangray.common.enums;

import java.util.Date;

/**
 * 设置定时任务周期
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/27/2018.
 */
public enum QuartzEnum {
    Mse500(500, "500毫秒"),
    Mse800(800, "800毫秒"),
    Second(1, "1秒钟"), //1秒钟
    Second3(3, "3秒钟"), //2秒钟
    Second5(5, "5秒钟"), //一秒钟
    Minute(100, "一分钟"), //一分钟
    Minute3(300, "三分钟"), //一分钟
    Hour(200, "一小时"),
    HourLastSec(202, "每小时最后一秒调用"),
    HourStart(201, "每小时开始的时候调用");//十小时;
//    Hour10(3);//十小时


    private Integer code;
    private String desc;

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }
    QuartzEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "QuartzEnum{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
