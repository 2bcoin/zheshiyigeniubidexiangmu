package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public class FMZOrder {
    public JsonNode Info;
    public String Id;//:交易单唯一标识
    public BigDecimal Price;//:下单价格
    public BigDecimal Amount;//:下单数量
    public BigDecimal DealAmount;//:成交数量
    public BigDecimal AvgPrice;//:成交均价，                     # 注意 ，有些交易所不提供该数据，不提供的设置为 0 。
    public Integer Status;//:订单状态, 参考常量里的订单状态
    public Integer Type;//:订单类型, 参考常量里的订单类型
}
