package com.github.misterchangray.service.common.vo.order.response;

public class OkexOrderBean {
    private Double amount;
    private Double avg_price;
    private Long create_date;
    private Integer deal_amount;
    private Long order_id;
    private Long orders_id;
    private Double price;
    private Integer status;  // -1:已撤销  0:未成交  1:部分成交  2:完全成交 3:撤单处理中
    private String symbol;
    private String type;


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAvg_price() {
        return avg_price;
    }

    public void setAvg_price(Double avg_price) {
        this.avg_price = avg_price;
    }

    public Long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Long create_date) {
        this.create_date = create_date;
    }

    public Integer getDeal_amount() {
        return deal_amount;
    }

    public void setDeal_amount(Integer deal_amount) {
        this.deal_amount = deal_amount;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getOrders_id() {
        return orders_id;
    }

    public void setOrders_id(Long orders_id) {
        this.orders_id = orders_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
        return "OkexOrderBean{" +
                "amount=" + amount +
                ", avg_price=" + avg_price +
                ", create_date=" + create_date +
                ", deal_amount=" + deal_amount +
                ", order_id=" + order_id +
                ", orders_id=" + orders_id +
                ", price=" + price +
                ", status=" + status +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
