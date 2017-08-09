package com.listore.commen;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/*
 * ������Ӧ����ģ��
 * ʵ��Serializable�ӿ�
 * 
 * */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
//��֤���л�json��ʱ�������null�Ķ���keyҲ����ʧ
public class ServerResponse<T> implements Serializable{
	private int status;
	private String msg;
	private T data;   //�п��ܻ��װ��map����list���͵����ݶ���
	private ServerResponse(int status){
		this.status = status;
	}
	private ServerResponse(int status,String msg){
		this.status = status;
		this.msg = msg;
	}
	private ServerResponse(int status,T data){
		this.status = status;
		this.data = data;
	}
	private ServerResponse(int status,String msg,T data){
		this.status = status;
		this.msg = msg;
		this.data = data;
	}
	@JsonIgnore
	//使之不在json序列化结果中
	public boolean isSuccess(){
		//���status = 0�Ļ�˵����¼�ɹ�
		return this.status == ResponseCode.SUCCESS.getCode();
	}
	
	public int getStatus(){
		return this.status;
	}
	//必须为get属性 的形式不然springMVC view层向客户端提供数据的时候找不到对应的get方法的时候会显示不出该数据
	public String getMsg(){
		return msg;
	}
	public T getData(){
		return data;
	}
	public static <T> ServerResponse<T> createBySuccess(){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}
	public static <T> ServerResponse<T> createBySuccessMsg(String msg){
		//System.out.println("msg is " + msg);
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
	}
	public static <T> ServerResponse<T> createBySuccess(T data){ 
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
	}
	public static <T> ServerResponse<T> createBySuccess(String msg,T data){
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
	}
	
	public static <T> ServerResponse<T> createByError(){
		return new ServerResponse<T>(ResponseCode.ERROR.getCode());
	}
	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
		//System.out.println("errorMessage is " + errorMessage);
		 return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
	}
	public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
		return new ServerResponse<T>(errorCode,errorMessage);
	}
}
