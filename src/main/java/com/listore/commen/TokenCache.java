package com.listore.commen;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/*
 * ���ػ���
 * */
public class TokenCache {
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
	public static final String TOKEN_PREFIX = "token_";
	
	//LRU算法
	private static LoadingCache<String,String> localCache  = CacheBuilder.newBuilder().maximumSize(10000).refreshAfterWrite(12, TimeUnit.HOURS)
			.build(new CacheLoader<String,String> (){
                //默认的数据加载实现，当掉用get取值的时候，如果key没有对应的值的时候，就会调用该方法
				@Override
				public String load(String arg0) throws Exception {
					// TODO Auto-generated method stub
					return "null";
				}
			}); 
	
		
    
		
	public static void setKey(String key,String value){
		try {
			System.out.println(localCache.get(key));
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		localCache.put(key, value);
		
	}
	public static String getKey(String key){
		String value = null;
		try{
			value = localCache.get(key);
			if("null".equals(value)){
				return null;
			}
			return value;
		}catch(Exception e){
		  logger.error("error",e);
		}
		return null;
	}
		           
	
}
