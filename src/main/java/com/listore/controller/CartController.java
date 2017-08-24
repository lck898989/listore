package com.listore.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;
import com.listore.pojo.User;
import com.listore.service.ICartService;

/*
 * 
 * 购物车模块开发
 * 
 * */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Resource(name="cartServer")
	private ICartService cartService;
	/*
	 * 
	 * 添加商品到购物车的方法
	 * 
	 * */
	@RequestMapping("/add_product_toCart")
	public ServerResponse add(HttpSession session,Integer userId,Integer prouductId,int count){
		//检查用户是否登录
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		//如果用户不存在的话返回一个提示信息
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.
		
	}
}
