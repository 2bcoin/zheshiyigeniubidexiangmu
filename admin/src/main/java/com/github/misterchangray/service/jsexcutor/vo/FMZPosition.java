package com.github.misterchangray.service.jsexcutor.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public class FMZPosition {
    public JsonNode Info;//交易所返回的原始结构
    public BigDecimal MarginLevel;//杆杠大小, OKCoin为10或者20,OK期货的全仓模式返回为固定的10, 因为原生API不支持
    public BigDecimal Amount;//持仓量,OKCoin表示合约的份数(整数且大于1)
    public BigDecimal CanCover;//可平量, 只有股票有此选项, 表示可以平仓的数量(股票为T+1)今日仓不能平
    public BigDecimal FrozenAmount;//仓位冻结量
    public BigDecimal Price;//持仓均价
    public BigDecimal Profit;//商品期货：持仓盯市盈亏，数字货币：(数字货币单位：BTC/LTC, 传统期货单位:RMB, 股票不支持此字段, 注: OKCoin期货全仓情况下指实现盈余, 并非持仓盈亏, 逐仓下指持仓盈亏)
    public Integer Type;//PD_LONG为多头仓位(CTP中用closebuy_today平仓), PD_SHORT为空头仓位(CTP用closesell_today)平仓, (CTP期货中)PD_LONG_YD为昨日多头仓位(用closebuy平), PD_SHORT_YD为昨日空头仓位(用closesell平)
    public String ContractType;//商品期货为合约代码, 股票为’交易所代码_股票代码’, 具体参数SetContractType的传入类型
}
