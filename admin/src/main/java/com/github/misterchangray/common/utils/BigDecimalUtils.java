package com.github.misterchangray.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    /**
     * 加
     */

    public static BigDecimal add(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2);
    }


    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2);
    }


    /**
     * 减
     */

    public static BigDecimal sub(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2);
    }

    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2);
    }

    /**
     * 乘
     */

    public static BigDecimal mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2);
    }

    /**
     * 乘
     */

    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2);
    }


    /**
     * 除
     */

    public static BigDecimal div(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        //四舍五入，保留36位小数
        return b1.divide(b2, 36, BigDecimal.ROUND_HALF_DOWN);
    }


    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        if(v2.doubleValue() == 0) return BigDecimal.valueOf(0);
        //四舍五入，保留9位小数
        return b1.divide(b2, 9, BigDecimal.ROUND_HALF_DOWN);
    }


    /**
     * @param args
     */

    public static void main(String[] args) {


    }


}

