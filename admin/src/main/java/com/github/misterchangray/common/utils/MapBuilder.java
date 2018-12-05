package com.github.misterchangray.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 返回对象为Map子类,可直接当Map使用
 * 简易的map构建器,用于链式构建map结构数据
 * 示例如下：
 * MapBuilder.build().add("key", "value").add("key2", "value2");
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/19/2018.
 * concurrenthashmap
 */
public class MapBuilder extends ConcurrentHashMap {

    private MapBuilder() {}

    public static MapBuilder build() {
        return new MapBuilder();
    }

    /**
     * 向map里增加元素
     * @param key
     * @param value
     * @return MapBuilder
     */
    public MapBuilder add(String key, Object value) {
        if(null == value) return this;
        super.put(key, value);
        return  this;
    }

    /**
     * 向map里增加元素
     * @param key
     * @param value
     * @return MapBuilder
     */
    public MapBuilder put(String key, Object value) {
        this.add(key, value);
        return  this;
    }

}
