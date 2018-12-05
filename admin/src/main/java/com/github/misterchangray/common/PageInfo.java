package com.github.misterchangray.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 页码信息类
 * 用于返回页码信息
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/23/2018.
 */
@ApiModel(description = "页码信息封装对象-PageInfo")
public class PageInfo {
    /**
     * 当前第几页
     */
    @ApiModelProperty(value = "当前第几页", dataType = "Integer")
    private Integer page;
    /**
     * 总共有多少条记录
     */
    @ApiModelProperty(value = "总共有多少条记录", dataType = "Integer")
    private Long count;
    /**
     * 每页多少数据
     */
    @ApiModelProperty(value = "每页多少数据", dataType = "Integer")
    private Integer limit;


    public PageInfo(Integer page, Long count, Integer limit) {
        this.page = page;
        this.count = count;
        this.limit = limit;
    }

    public static PageInfo newInstance(Integer page, Integer limit) {
        return  new PageInfo().setPage(page).setLimit(limit);
    }
    public static PageInfo newInstance() {
        return  new PageInfo();
    }

    public PageInfo() {
        //        默认每页10条，第一页开始
        this.page = 0;
        this.limit = 10;
    }
    public PageInfo(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public PageInfo setPage(Integer page) {
        this.page = page;
        return  this;
    }

    public Long getCount() {
        return count;
    }

    public PageInfo setCount(Long count) {
        this.count = count;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public PageInfo setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
