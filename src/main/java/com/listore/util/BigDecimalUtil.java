package com.listore.util;

import java.math.BigDecimal;

/**
 * Created by HP on 2017/8/26.
 */
/*
* 将数据库中的deboule类型的数据转成字符创类型
* 利于商业上的精度要求
*
* */
public class BigDecimalUtil {
    //不能被外部实例化
    private BigDecimalUtil(){

    }
    //加法
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }
    //减法
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    //乘法
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    //除法时候要进行四舍五入或者保留小数位数
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
