package com.github.misterchangray.service.common.vo.order.response;

import java.util.Map;

public class YobitResponse {
    private Long server_time;
    private Map<String,Object> pairs;

    public Long getServer_time() {
        return server_time;
    }

    public void setServer_time(Long server_time) {
        this.server_time = server_time;
    }

    public Map<String, Object> getPairs() {
        return pairs;
    }

    public void setPairs(Map<String, Object> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String toString() {
        return "YobitResponse{" +
                "server_time='" + server_time + '\'' +
                ", pairs=" + pairs +
                '}';
    }
}
