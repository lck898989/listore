package com.li.test;

public class testMain {
	 public static void main(String[] args) {
		testMain tm = new testMain();
		System.out.println(tm.test());
	}
	public  int test(){
		
		try{
			return function1();
		}finally{
			return function2();
		}
		
	}
	public int  function1(){
		System.out.println("function1");
		return 1;
	}
	public int function2(){
		System.out.println("function2");
		return 2;
	}
}
