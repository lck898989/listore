package com.listore.service;

import org.springframework.web.multipart.MultipartFile;

/*
 * 
 * 文件上传处理的服务
 * 
 * */
public interface IFileService {
	public String  upload(MultipartFile mpfile,String path);
	
}
