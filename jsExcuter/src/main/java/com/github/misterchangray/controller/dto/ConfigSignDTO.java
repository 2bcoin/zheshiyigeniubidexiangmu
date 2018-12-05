package com.github.misterchangray.controller.dto;

/**
 * 配置签名对象
 */
public class ConfigSignDTO {
    private String ak;
    private String sign;

    public ConfigSignDTO(String ak, String sign) {
        this.ak = ak;
        this.sign = sign;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAk() {
        return ak;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
