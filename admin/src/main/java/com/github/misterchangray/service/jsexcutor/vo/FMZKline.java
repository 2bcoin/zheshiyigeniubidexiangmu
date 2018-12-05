package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public class FMZKline {
    public JsonNode Info;
    public Long Time;//	一个时间戳, 精确到毫秒，与Javascript的 new Date().getTime() 得到的结果格式一样
    public BigDecimal Open;//	开盘价
    public BigDecimal High;//	最高价
    public BigDecimal Low;//	最低价
    public BigDecimal Close;//	收盘价
    public BigDecimal Volume;//	交易量
}
