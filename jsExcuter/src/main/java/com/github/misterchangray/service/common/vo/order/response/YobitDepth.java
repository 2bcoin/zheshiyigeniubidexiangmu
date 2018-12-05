package com.github.misterchangray.service.common.vo.order.response;

import java.util.List;

public class YobitDepth {
    private List asks;
    private List bids;

    public List getAsks() {
        return asks;
    }

    public void setAsks(List asks) {
        this.asks = asks;
    }

    public List getBids() {
        return bids;
    }

    public void setBids(List bids) {
        this.bids = bids;
    }

    @Override
    public String toString() {
        return "YobitDepth{" +
                "asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
