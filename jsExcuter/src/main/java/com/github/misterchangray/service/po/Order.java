package com.github.misterchangray.service.po;

import com.github.misterchangray.common.ResultSet;

import java.math.BigDecimal;

public class Order {
    public static String collectionName = "orders";
    public static String direc_buy = "buy";
    public static String direc_sell = "sell";
    public static String direc_cancel = "cancel";

    private String date;    //订单操作时间
    private String org_name; //机构一名
    private String org_type;//交易对
    private String org_direc;//买卖方向
    private BigDecimal org_price;//交易价格
    private BigDecimal org_count;//交易数量
    private String order_id;    //交易成功后此字段有值
    private String org_aKey;//买卖方向
    private String org_sKey;//买卖方向
    private String lr; //利润
    private String lv; //利率

    private ResultSet<String> resultSet;    //此字段保存服务器返回值

    public ResultSet<String> getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet<String> resultSet) {
        this.resultSet = resultSet;
        this.order_id = resultSet.getData();
    }

    public String getOrg_aKey() {
        return org_aKey;
    }

    public void setOrg_aKey(String org_aKey) {
        this.org_aKey = org_aKey;
    }

    public String getOrg_sKey() {
        return org_sKey;
    }

    public void setOrg_sKey(String org_sKey) {
        this.org_sKey = org_sKey;
    }

    public Order(String org_name, String org_type, String org_direc, BigDecimal org_price, BigDecimal org_count, String akey, String sKey) {
        this.org_name = org_name;
        this.org_type = org_type;
        this.org_direc = org_direc;
        this.org_price = org_price;
        this.org_count = org_count;
        this.org_aKey = akey;
        this.org_sKey = sKey;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public static String getCollectionName() {
        return collectionName;
    }

    public static void setCollectionName(String collectionName) {
        Order.collectionName = collectionName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLr() {
        return lr;
    }

    public void setLr(String lr) {
        this.lr = lr;
    }

    public String getLv() {
        return lv;
    }

    public void setLv(String lv) {
        this.lv = lv;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getOrg_type() {
        return org_type;
    }

    public void setOrg_type(String org_type) {
        this.org_type = org_type;
    }

    public String getOrg_direc() {
        return org_direc;
    }

    public void setOrg_direc(String org_direc) {
        this.org_direc = org_direc;
    }

    public BigDecimal getOrg_price() {
        return org_price;
    }

    public void setOrg_price(BigDecimal org_price) {
        this.org_price = org_price;
    }

    public BigDecimal getOrg_count() {
        return org_count;
    }

    public void setOrg_count(BigDecimal org_count) {
        this.org_count = org_count;
    }
}
