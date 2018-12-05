package com.github.misterchangray.service.common.vo.order.response.fcoin;

import java.util.List;

/**
 * Created by riecard on 2018/6/17.
 */
public class FcoinBitinfoResponse {

    private Integer status;
    private List<FcoinBitinfo> data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FcoinBitinfo> getData() {
        return data;
    }

    public void setData(List<FcoinBitinfo> data) {
        this.data = data;
    }
}
