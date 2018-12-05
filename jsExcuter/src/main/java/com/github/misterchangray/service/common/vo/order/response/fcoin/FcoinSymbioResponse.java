package com.github.misterchangray.service.common.vo.order.response.fcoin;

import java.util.List;

/**
 * Created by riecard on 2018/6/17.
 */
public class FcoinSymbioResponse {
    private Integer status;
    private List<FcoinSymbio> data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<FcoinSymbio> getData() {
        return data;
    }

    public void setData(List<FcoinSymbio> data) {
        this.data = data;
    }
}
