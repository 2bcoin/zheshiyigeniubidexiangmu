package com.github.misterchangray.service.jsexcutor.vo;

import java.math.BigDecimal;

public class FMZTrade {
    public String	Id;	//string 或 number （根据交易所返回类型而定）交易所返回的此Trade的唯一Id
    public BigDecimal Time;//	时间Unix timestamp 毫秒
    public BigDecimal	Price;//	价格
    public BigDecimal	Amount;//	数量
    public Integer	Type;//	订单类型:ORDER_TYPE_BUY, ORDER_TYPE_SELL。分别为买单，值为0，卖单，值为1
}
