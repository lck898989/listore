package com.listore.test;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by HP on 2017/8/26.
 */
/*
*
* 测试BigDecimal
*
* */
public class BigDecimalTest {
    //测试方法必须是公共的方法
    @Test
    public void test1(){
         System.out.println(0.05 + 0.01);
        System.out.println(1 - 0.43);
        System.out.println(1.015*100);
        System.out.println(123.3/100);
    }
    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));
    }
    @Test
    public void test3(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }
    @Test
    public void test4(){

        System.out.println(new BigDecimal(4299).equals(new BigDecimal(4299)));
    }
}
