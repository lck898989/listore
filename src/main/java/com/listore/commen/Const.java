package com.listore.commen;


import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
	public static final String CURRENT_USER = "currentUser";
	public static final String EMAIL = "email";
	public static final String USERNAME = "username";
	public interface productOrder{
		Set<String> productOrder = Sets.newHashSet("price_desc","price_asc");
	}
	public interface Cart{
		int CHECKED = 1;
		int UN_CHECKED = 0;
		String 	LIMIT_FAIL="LIMIT_FAIL";
		String LIMIT_SUCCESS="LIMIT_SUCCESS";
		
	}
	public interface Role {
		int ROLE_CUSTOM = 0;// 普通用户
		int ROLE_ADMIN = 1; //管理员
	}
	public enum ProductStatusEnum {
	    ON_SALE("在售",1);
		private String value;
		private int code;
		ProductStatusEnum(String value,int code){
			this.value = value;
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
		
	}
	public enum OrderStatusEnum{
		CANCELED(0,"已取消"),
		NO_PAY(10,"未支付"),
		PAID(20,"已付款"),
		SHIPPED(40,"已发货"),
		ORDER_SUCCESS(50,"订单完成"),
		ORDER_CLOSE(60,"订单关闭");
		OrderStatusEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		private String value;
		private int code;

		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
		public static OrderStatusEnum codeOf(int code){
			for(OrderStatusEnum orderStatusEnum : values()){
				if(orderStatusEnum.getCode() == code){
					return orderStatusEnum;
				}
			}
			throw  new RuntimeException("没有该状态码的对应描述");

		}

	}
	public interface AlipayCallback{
		String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
		String TARDE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

		String RESPONSE_SUCESS = "success";
		String RESPONSE_FAILED = "failed";
	}
	public enum PayPlatformEnum{
		ALIPAY(1,"支付宝"),
		WECHAT(2,"微信");
		private String value;
		private int code;
        PayPlatformEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
	}
	public enum PaymentTypeEnum{
		ONLINE(1,"在线支付");
		private String value;
		private int code;
		PaymentTypeEnum(int code,String value){
			this.code = code;
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
		public static PaymentTypeEnum codeOf(int code){
			for(PaymentTypeEnum paymentTypeEnum:values()){
				if(paymentTypeEnum.getCode() == code){
					return paymentTypeEnum;
				}
			}
			throw new RuntimeException("没有找到该状态码对应的枚举类型");
		}
	}
}