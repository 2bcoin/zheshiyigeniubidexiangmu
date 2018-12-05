package com.github.misterchangray.common;

import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.utils.JSONUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 通用结果集
 * 用于ajax返回
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/20/2018.
 */
@ApiModel(description = "标准返回封装-ResultSet")
public class ResultSet<T> {
    /**
     * 返回消息
     */
    @ApiModelProperty(value = "消息信息", dataType = "String")
    private String msg;
    /**
     * 结果代码,参见 ResultEnum
     */
    @ApiModelProperty(value = "结果代码;0为成功;非0失败", dataType = "Integer")
    private Integer code;
    /**
     * 返回的数据,这里一般是函数的返回值
     */
    @ApiModelProperty(value = "结果返回 JSON 格式", dataType = "JSON")
    private T data;
    /**
     * 页码信息
     */
    @ApiModelProperty(value = "页码信息", dataType = "PageInfo")
    private PageInfo pageInfo;



    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public boolean isSuccess() {
        return code == 0;
    }

    public ResultSet setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
        return this;
    }

    public ResultSet setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResultSet setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResultSet setCode(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public ResultSet setData(T data) {
        this.data = data;
        return this;
    }

    public T getData() {
        return data;
    }


    public static ResultSet build() {
        ResultSet resultSet = new ResultSet();
        resultSet.setCode(ResultEnum.SUCCESS.getCode());
        resultSet.setMsg(ResultEnum.SUCCESS.getMsg());
        return resultSet;
    }

    public static ResultSet build(ResultEnum resultEnum) {
        ResultSet resultSet = new ResultSet();
        if(null != resultEnum) {
            resultSet.setCode(resultEnum.getCode());
            resultSet.setMsg(resultEnum.getMsg());
        } else {
            resultSet.setCode(ResultEnum.SERVER_ERROR.getCode());
            resultSet.setMsg(ResultEnum.SERVER_ERROR.getMsg());
        }
        return resultSet;
    }

    @Override
    public String toString() {
        return JSONUtils.obj2json(this);
    }

    private ResultSet() {}
}


