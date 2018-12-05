package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 * "id": "9d17a03b852e48c0b3920c7412867623",
 "symbol": "string",
 "type": "limit",
 "side": "buy",
 "price": "string",
 "amount": "string",
 "state": "submitted",
 "executed_value": "string",
 "fill_fees": "string",
 "filled_amount": "string",
 "created_at": 0,
 "source": "web"
 */
public class FcoinOrder {
    private String id;
    private String symbol;
    private String type;
    private String side;
    private String price;
    private String state;
    private String amount;
    private String executed_value;
    private String fill_fees;
    private String filled_amount;
    private Long created_at;
    private String source;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExecuted_value() {
        return executed_value;
    }

    public void setExecuted_value(String executed_value) {
        this.executed_value = executed_value;
    }

    public String getFill_fees() {
        return fill_fees;
    }

    public void setFill_fees(String fill_fees) {
        this.fill_fees = fill_fees;
    }

    public String getFilled_amount() {
        return filled_amount;
    }

    public void setFilled_amount(String filled_amount) {
        this.filled_amount = filled_amount;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
