package com.github.misterchangray.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.weaver.BCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 常用静态工具类
 * JSON相关操作工具
 *
 *
 * 连续访问一个JSON的多个属性时推荐先使用 buildJsonNode 方法建立 Json实体;再读取其中的属性;以提高效率
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 2018/4/23.
 */
public class JSONUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    static  Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static void main(String[] a) {


//
//        System.out.print(JSONUtils.json2obj("{\"id\":\"123\"}", User.class));;
//        User u = new User();
//        u.setId(123123);
//
//        AjaxResultSet<User> n = AjaxResultSet.build().setData(u);
//        User u2 = n.getData();
//        System.out.println(u2);
//        System.out.println("1a1".split("\\.").length);
        //{a:1,b:{e:[{},{t:"asd"}]}}
//
//
//        String json = "{\"a\":1,\"b\":{\"e\":[{},{\"t\":\"asd\"}]}}";
//        json = "[{\"id\":1,\"username\":\"zr\"},{\"id\":2,\"username\":\"zr2\"}]";
//        Long start = System.currentTimeMillis();
//        JsonNode jsonNode2 = getJsonPathVal(json, "a.h", "");
//        System.out.println(jsonNode2.isNull());
//        System.out.println(getJsonPathVal(json, "b", "e"));
//        System.out.println(getJsonPathVal(json, "b.e", "e"));
//        System.out.println(getJsonPathVal(json, "b.e.t", "e"));
//        System.out.println("-1-----------" + (System.currentTimeMillis() - start));
//
//        start = System.currentTimeMillis();
//        JsonNode jsonNode = buildJsonNode(json);
////        System.out.println(getJsonPathVal(jsonNode, "[1].username2", "e"));
//        System.out.println(getJsonPathVal(jsonNode, "b", "e"));
//        System.out.println(getJsonPathVal(jsonNode, "b.e", "e"));
//        System.out.println(getJsonPathVal(jsonNode, "b.e[1].t", false));
//        System.out.println("-2-----------" + (System.currentTimeMillis() - start));


    }


    /**
     * 构建json树
     * @param json
     * @return
     */
    public static JsonNode buildJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("json内容错误;{}", json);

        }
        return null;
    }



    /**
     *
     * 快捷获取json对象属性; 当获取对象多个属性时候;请搭配buildJsonNode使用
     * 注意;不能直接获取
     * @param json 字符串json数据;或JsonNode对象
     * @param propName 待获取属性名称
     * @param defaultVal 待获取属性未定义则返回此值
     * @returns {Object}, 获取的属性值
     * @return
     */
    public static JsonNode getJsonPathVal(Object json, String propName, Object defaultVal) {
        String[] keys;
        JsonNode rootNode = null;
        if(null == propName) return mapper.valueToTree(defaultVal);
        if(null != json && json instanceof JsonNode) {
            rootNode = (JsonNode) json;
        } else if(json instanceof String){
            try {
                rootNode = mapper.readTree(String.valueOf(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!propName.matches("^([a-zA-Z0-9_$]*?(\\[\\d+\\])?\\.?)+$")) {
            throw new RuntimeException("属性名称表达式语法错误");
        }

        keys = propName.split("\\.");
        Integer index;
        String key;
        for(int i=0, j = keys.length; i<j; i++) {
            key = keys[i];
            if(key.contains("[") && key.contains("]")) {

                index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
                key = key.substring(0, key.indexOf("["));
                if(null == key || 0 == key.length()) {
                    rootNode = rootNode.get(index);
                } else {
                    rootNode = rootNode.get(key);
                    if(null == rootNode)  return mapper.valueToTree(defaultVal);
                    rootNode = rootNode.get(index);
                }
            } else {
                rootNode = rootNode.get(key);
            }
            if(null == rootNode)  return mapper.valueToTree(defaultVal);
        }


        return rootNode;
    }



    /**
     * 对象转换为JSON字符串
     * @param object
     * @return
     * @throws BCException
     */
    public static String obj2json(Object object) {
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * json转map
     * @param json
     * @return
     * @throws BCException
     */
    public static Map json2map(String json) {
        Map res = null;
        try {
            //忽略不存在的字段
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            res = mapper.readValue(json, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * json转List<T>
     * @param json
     * @return
     * @throws BCException
     */
    public static <T> List<T> json2list(String json, Class clazz) {
        List<T> configList = null;
        try {
            //忽略不存在的字段
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
            configList =  mapper.readValue(json, javaType);   //这里不需要强制转换
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configList;
    }

    /**
     * json转到指定对象
     * @param json
     * @return
     * @throws BCException
     */
    public static <T> T json2obj(String json, Class<T> t) {
        T res = null;
        try {
            //忽略不存在的字段
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            res = (T) mapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
