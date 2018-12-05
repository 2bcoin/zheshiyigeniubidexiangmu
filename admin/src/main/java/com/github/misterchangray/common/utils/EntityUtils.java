package com.github.misterchangray.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;


/**
 * 常用静态工具类
 * 提供对象处理方法
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/26/2018.
 */
public class EntityUtils {
    /**
     * 获取对象值为null的属性
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 对象属性拷贝,从右拷贝到左,拷贝时忽略null的属性
     * @param source
     * @param target
     */
    public static void copyPropertiesIgnoreNull(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }


    /**
     * 对象属性拷贝,从右拷贝到左
     * 注意这个函数也回拷贝null值
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }


}
