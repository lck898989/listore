package com.listore.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
/*
 * 处理时间类型的工具类
 * 
 * */
public class TimeUtil {
	private static final String DATETIME_FORMATSTR = "yyyy-MM-dd HH:mm:ss";
	//两个方法 1：  将字符串类型的变量装换成时间类型
    //2 ：将时间类型转换为字符串类型
	public static Date strToDate(String timeStr,String formatStr){
		/*SimpleDateFormat dft = new SimpleDateFormat(formatStr);
		try {
			Date date = dft.parse(timeStr);
			if(date != null){
				return date;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
		DateTime dateTime = dateTimeFormatter.parseDateTime(timeStr);
		return dateTime.toDate();
	}
	//将Date对象转换为字符串类型
	public static String dateToStr(Date date,String formatStr){
		if(date == null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(formatStr);
	}
	public static Date strToDate(String timeStr){
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATETIME_FORMATSTR);
		DateTime dateTime = dateTimeFormatter.parseDateTime(timeStr);
		return dateTime.toDate();
	}
	//将Date对象转换为字符串类型
	public static String dateToStr(Date date){
		if(date == null){
			return StringUtils.EMPTY;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(DATETIME_FORMATSTR);
	}
	/*public static void main(String[] args) {
		System.out.println(TimeUtil.strToDate("2017-08-13 21:05:00","yyyy-MM-dd HH:mm:ss"));
	    System.out.println(TimeUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
	    System.out.println(new Date().getMonth());
	}*/
}
