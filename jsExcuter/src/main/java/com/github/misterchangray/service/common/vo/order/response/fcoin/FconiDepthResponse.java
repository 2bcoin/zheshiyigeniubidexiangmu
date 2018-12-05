package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 */
public class FconiDepthResponse {

    private Integer status;
    private FconiDepth data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public FconiDepth getData() {
        return data;
    }

    public void setData(FconiDepth data) {
        this.data = data;
    }
}
