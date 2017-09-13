package com.listore.service;

import com.listore.vo.CartVo;
import org.springframework.stereotype.Service;

import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;


public interface ICartService {
	ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count);

	ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

	ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

	ServerResponse<CartVo> listProduct(Integer UserId);

	ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked);

	ServerResponse<Integer> selectProductCountInCart(Integer id);
}
