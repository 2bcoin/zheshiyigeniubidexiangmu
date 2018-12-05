package com.github.misterchangray.service.common.vo.order.response.fcoin;

import java.util.List;

/**
 * Created by riecard on 2018/6/17.
 */
public class FconiDepth {
    private String type;
    private Integer seq;
    private Long ts;
    private List<Double> bids;
    private List<Double> asks;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public List<Double> getBids() {
        return bids;
    }

    public void setBids(List<Double> bids) {
        this.bids = bids;
    }

    public List<Double> getAsks() {
        return asks;
    }

    public void setAsks(List<Double> asks) {
        this.asks = asks;
    }
}
