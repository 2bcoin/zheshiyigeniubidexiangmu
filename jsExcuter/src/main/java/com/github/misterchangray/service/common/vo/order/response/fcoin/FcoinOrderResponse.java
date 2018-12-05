package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 */
public class FcoinOrderResponse {

    private Integer status;
    private FcoinOrder data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public FcoinOrder getData() {
        return data;
    }

    public void setData(FcoinOrder data) {
        this.data = data;
    }
}
