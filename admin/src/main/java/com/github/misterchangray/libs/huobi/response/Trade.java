package com.github.misterchangray.libs.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 14:52
 */

public class Trade<T> {

    /**
     * id : 600848670
     * ts : 1489464451000
     * data : [{"id":600848670,"price":7962.62,"amount":0.0122,"direction":"buy","ts":1489464451000}]
     */

    private long id;
    private long ts;
    private T data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
