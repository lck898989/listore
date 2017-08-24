package com.listore.service;

import org.springframework.stereotype.Service;

import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;


public interface ICartService {
	ServerResponse add(Integer userId,Integer productId,int count);

}
