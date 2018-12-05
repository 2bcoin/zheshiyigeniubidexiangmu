package com.github.misterchangray.service.common.vo.order.response.fcoin;

/**
 * Created by riecard on 2018/6/17.
 * "status": 0,
 "msg": "string",
 "data": true
 */
public class FcoinCancleOrderResponse {
    private Integer status;
    private String msg;
    private Boolean data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }
}
