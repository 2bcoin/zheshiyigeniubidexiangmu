package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public class FMZTicker {
    public  JsonNode Info; //	交易所返回的原始结构
    public  BigDecimal High;	//最高价
    public  BigDecimal	Low	;//最低价
    public  BigDecimal	Sell;	//卖一价
    public  BigDecimal	Buy;//	买一价
    public  BigDecimal	Last;//	最后成交价
    public  BigDecimal	Volume;//	最近成交量


}
