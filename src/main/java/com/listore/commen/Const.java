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
}