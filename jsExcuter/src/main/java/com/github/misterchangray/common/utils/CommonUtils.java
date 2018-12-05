package com.github.misterchangray.common.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class CommonUtils {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {
        if (map == null || map.size() <= 0)
            return null;

        Object obj = beanClass.newInstance();
        //获取关联的所有类，本类以及所有父类
        boolean ret = true;
        Class oo = obj.getClass();
        List<Class> clazzs = new ArrayList<Class>();
        while (ret) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
            if (oo == null || oo == Object.class) break;
        }

        for (int i = 0; i < clazzs.size(); i++) {
            Field[] fields = clazzs.get(i).getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                //由字符串转换回对象对应的类型
                if (field != null) {
                    field.setAccessible(true);
                    field.set(obj, map.get(field.getName()));
                }
            }
        }
        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj, String... ignoreProperties) {
        if (obj == null) {
            return null;
        }
        //获取关联的所有类，本类以及所有父类
        boolean ret = true;
        Class oo = obj.getClass();
        List<Class> clazzs = new ArrayList<Class>();
        while (ret) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
            if (oo == null || oo == Object.class) break;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (Class clazz : clazzs) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                //过滤 static 和 final 类型
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || (ignoreList != null && ignoreList.contains(field.getName()))) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    map.put(field.getName(), field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }
}
