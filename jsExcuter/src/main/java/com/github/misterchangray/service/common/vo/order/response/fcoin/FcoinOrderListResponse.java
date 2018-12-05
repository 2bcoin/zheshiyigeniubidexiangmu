package com.github.misterchangray.service.common.vo.order.response.fcoin;

import java.util.List;

/**
 * Created by riecard on 2018/6/17.
 */
public class FcoinOrderListResponse {
    private Integer status;
    private List<FcoinOrder> data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FcoinOrder> getData() {
        return data;
    }

    public void setData(List<FcoinOrder> data) {
        this.data = data;
    }
}
