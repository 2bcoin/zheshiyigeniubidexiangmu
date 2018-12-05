package com.github.misterchangray.common.utils;

import java.util.ArrayList;

/**
 * @author Created by rui.zhang on 2018/5/24.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 */
public class ListBuilder<T> extends ArrayList {
    /**
     * new一个List实例
     * @return
     */
    public static ListBuilder build() {
        return new ListBuilder();
    }


    /**
     * 为List增加一个元素
     * @param obj
     * @return
     */
    public ListBuilder append(Object obj) {
        super.add(obj);
        return this;
    }


    /**
     * 次函数主要解决字符串拼接问题,例如拼接过程中要加逗号,空值不拼接等等
     * 格式化输出
     * @return
     */
    public String getText(String p) {
        if(null == p) p = ", ";
        StringBuilder stringBuilder = new StringBuilder();
        for(Object o : this) {
            if(null == o || "".equals(o.toString().trim())) continue;;
            stringBuilder.append(p + o.toString());
        }
        if(stringBuilder.toString().length() > 2 && null != p) {
            return stringBuilder.toString().substring(p.length());
        } else {
            return stringBuilder.toString();
        }
    }

}
