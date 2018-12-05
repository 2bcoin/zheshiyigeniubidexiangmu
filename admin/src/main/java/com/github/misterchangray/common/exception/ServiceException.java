package com.github.misterchangray.common.exception;

import com.github.misterchangray.common.enums.ResultEnum;

/**
 * @author Created by rui.zhang on 2018/6/4.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 * 封装全局统一异常返回；
 * 此类作为业务异常返回封装
 * 你可以在代码中任何地方返回错误信息到前端
 */
public class ServiceException extends Exception {
    private String msg;
    private ResultEnum resultEnum;

    public ServiceException(ResultEnum resultEnum, String errorMsg) {
        super();
        this.resultEnum = resultEnum;
        this.msg = resultEnum.getMsg();

        if(null != errorMsg) {
            this.msg = errorMsg;
        }
    }

    public ServiceException(ResultEnum resultEnum) {
        super();
        this.resultEnum = resultEnum;
        this.msg = resultEnum.getMsg();

    }

    public ServiceException(String errorMsg) {
        super();
        this.msg = errorMsg;
    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }


    @Override
    public String toString() {
        return "ServiceException{" +
                "msg='" + msg + '\'' +
                ", resultEnum=" + resultEnum +
                '}';
    }
}