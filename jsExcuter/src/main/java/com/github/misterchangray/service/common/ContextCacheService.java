package com.github.misterchangray.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;

/**
 *
 * 用于全局缓存;目前采用ServletContext;大量数据推荐使用redis
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/29/2018.
 */
@Service
public class ContextCacheService {
    @Autowired
    ServletContext servletContext;

    /**
     * 缓存一个对象
     * @param key
     * @param value
     * @return
     */
    public boolean add(String key, Object value) {
        if(null != servletContext.getAttribute(key)) return false;
        servletContext.setAttribute(key, value);
        return true;
    }


    /**
     * 缓存数据;可覆盖原始数据
     * @param key
     * @param value
     * @param override
     * @return
     */
    public boolean add(String key, Object value, boolean override) {
        if(null != servletContext.getAttribute(key) && false == override) return false;
        servletContext.setAttribute(key, value);
        return true;
    }


    /**
     * 移除缓存
     *
     * @param key
     */
    public void remove(String key) {
        this.servletContext.removeAttribute(key);
    }


    /**
     * 判断key是否已经存在
     * @param key
     * @return
     */
    public boolean exist(String key) {
        if(null == servletContext.getAttribute(key)) return false;
        return true;
    }


    /**
     * 根据key获取对象
     * @param key
     * @return
     */
    public Object get(String key) {
        return servletContext.getAttribute(key);
    }

}
