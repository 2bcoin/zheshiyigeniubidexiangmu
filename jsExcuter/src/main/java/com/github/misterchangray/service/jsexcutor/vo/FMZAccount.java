package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

public class FMZAccount {
    public JsonNode Info;//	交易所返回的原始结构
    public Double	Balance	;//余额(定价货币余额, ETH_BTC的话BTC为定价货币)
    public Double	FrozenBalance;//	冻结的余额
    public Double	Stocks;//	交易货币的可用数量, 数字货币现货为当前可操作币的余额(去掉冻结的币), 数字货币期货的话为合约当前可用保证金(传统期货无此属性) 。
    public Double	FrozenStocks;//	冻结的交易货币的可用数量(传统期货无此属性)
}
