package com.github.misterchangray.common.utils;


/**
 * 对字符串数组进行排序
 * @author panjianghong
 * @since 2016/8/31
 * */
public class StringSort {


    /**
     * 次函数主要解决字符串拼接问题,例如拼接过程中要加逗号,空值不拼接等等
     * 格式化输出
     * @return
     */
    public static String getText(String[] tmp, String p) {
        if(null == p) p = ", ";
        StringBuilder stringBuilder = new StringBuilder();
        for(Object o : tmp) {
            if(null == o || "".equals(o.toString().trim())) continue;;
            stringBuilder.append(p + o.toString());
        }
        if(stringBuilder.toString().length() > 2 && null != p) {
            return stringBuilder.toString().substring(p.length());
        } else {
            return stringBuilder.toString();
        }
    }


    /**
     * 对字符串数组进行排序
     * @param keys
     * @return
     * */
    public static String[] getUrlParam(String[] keys){

        for (int i = 0; i < keys.length - 1; i++) {
            for (int j = 0; j < keys.length - i -1; j++) {
                String pre = keys[j];
                String next = keys[j + 1];
                if(isMoreThan(pre, next)){
                    String temp = pre;
                    keys[j] = next;
                    keys[j+1] = temp;
                }
            }
        }
        return keys;
    }

    /**
     * 比较两个字符串的大小，按字母的ASCII码比较
     * @param pre
     * @param next
     * @return
     * */
    private static boolean isMoreThan(String pre, String next){
        if(null == pre || null == next || "".equals(pre) || "".equals(next)){
            return false;
        }

        char[] c_pre = pre.toCharArray();
        char[] c_next = next.toCharArray();

        int minSize = Math.min(c_pre.length, c_next.length);

        for (int i = 0; i < minSize; i++) {
            if((int)c_pre[i] > (int)c_next[i]){
                return true;
            }else if((int)c_pre[i] < (int)c_next[i]){
                return false;
            }
        }
        if(c_pre.length > c_next.length){
            return true;
        }

        return false;
    }


    public static void main(String[] args) {

        String[] keys = getUrlParam(new String[]{"fin","abc","shidema","shide","bushi"});

        for (String key : keys) {
            System.out.println(key);
        }

    }
}