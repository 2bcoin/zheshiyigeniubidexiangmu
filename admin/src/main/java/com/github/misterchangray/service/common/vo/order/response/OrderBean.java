package com.github.misterchangray.service.common.vo.order.response;


import java.io.Serializable;

public class OrderBean implements Serializable {
    private String amount;
    private String order_id;
    private String price;
    private String status;  // -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中 4:部分成交撤销
    private String symbol;
    private String type;
    //接到撤单申请的时间
    private String canceled_at;
    //订单创建时间
    private String created_at;
    //已成交数量
    private String field_amount;
    //已成交总金额
    private String field_cash_amount;
    //已成交手续费（买入为币，卖出为钱）
    private String field_fees;
    //最后成交时间
    private String finishe_at;

    public String getCanceled_at() {
        return canceled_at;
    }

    public void setCanceled_at(String canceled_at) {
        this.canceled_at = canceled_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getField_amount() {
        return field_amount;
    }

    public void setField_amount(String field_amount) {
        this.field_amount = field_amount;
    }

    public String getField_cash_amount() {
        return field_cash_amount;
    }

    public void setField_cash_amount(String field_cash_amount) {
        this.field_cash_amount = field_cash_amount;
    }

    public String getField_fees() {
        return field_fees;
    }

    public void setField_fees(String field_fees) {
        this.field_fees = field_fees;
    }

    public String getFinishe_at() {
        return finishe_at;
    }

    public void setFinishe_at(String finishe_at) {
        this.finishe_at = finishe_at;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "amount='" + amount + '\'' +
                ", order_id='" + order_id + '\'' +
                ", price='" + price + '\'' +
                ", status='" + status + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", canceled_at='" + canceled_at + '\'' +
                ", created_at='" + created_at + '\'' +
                ", field_amount='" + field_amount + '\'' +
                ", field_cash_amount='" + field_cash_amount + '\'' +
                ", field_fees='" + field_fees + '\'' +
                ", finishe_at='" + finishe_at + '\'' +
                '}';
    }
}
