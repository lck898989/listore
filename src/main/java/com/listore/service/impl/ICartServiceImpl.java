package com.listore.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.listore.commen.Const;
import com.listore.commen.ServerResponse;
import com.listore.dao.CartMapper;
import com.listore.pojo.Cart;
import com.listore.service.ICartService;
@Service("cartServer")
public class ICartServiceImpl implements ICartService {
	@Resource
	private CartMapper cartMapper;
	/*
	 * 添加商品到购物车中
	 * 
	 * */

	@Override
	public ServerResponse add(Integer userId, Integer productId, int count) {
		//先判断购物车里面有没有该对象存在
		Cart cart = cartMapper.selectByProductIdUserId(userId,productId);
		if(cart == null){
			Cart cartItem = new Cart();
			cart.setProductId(productId);
			cart.setUserId(userId);
			cart.setChecked(Const.Cart.CHECKED);
			cart.setQuantity(count);
			//说明购物车里面没有该商品可以添加
			cartMapper.insertSelective(cart);
		}
		return null;
	}
	
}
