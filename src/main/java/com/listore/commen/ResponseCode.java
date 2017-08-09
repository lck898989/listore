package com.listore.commen;

public enum ResponseCode {
      SUCCESS(0,"SUCCESS"),
      ERROR(1,"ERROR"),
      NEED_LOGIN(10,"NEED_LOGIN"),
      ILLEGALE_ARGUMENT(2,"ILLEGALE_ARGUMENT");
      
      private int code;
	  private String desc;
	  ResponseCode(int code,String desc){
		  this.code = code;
		  this.desc = desc;
	  }
	  public int getCode(){
		  return this.code;
	  }
	  public String getDesc(){
		  return desc;
	  }
}
