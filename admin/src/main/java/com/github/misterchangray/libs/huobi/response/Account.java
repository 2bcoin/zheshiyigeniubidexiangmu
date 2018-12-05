package com.github.misterchangray.libs.huobi.response;

/**
 * @Author ISME
 * @Date 2018/1/14
 * @Time 16:02
 */
public class Account {
    public long id;
    public String type;
    public String state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
