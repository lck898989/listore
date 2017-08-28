package com.listore.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.listore.vo.CartVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;
import com.listore.pojo.User;
import com.listore.service.ICartService;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * 
 * 购物车模块开发
 * 
 * */
@Controller
@RequestMapping("/cart")
public class CartController {
	@Resource(name = "cartServer")
	private ICartService cartService;

	/*
	 * 
	 * 添加商品到购物车的方法
	 * 核心方法，涉及到主要方法getCartVo()的封装
	 * 
	* */
	@RequestMapping("/add_product_toCart")
	@ResponseBody
	public ServerResponse<CartVo> add(HttpSession session, Integer productId, int count) {
		//检查用户是否登录,防止横向越权纵向越权
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		//如果用户为空的话返回一个提示信息
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		return cartService.add(user.getId(), productId, count);

	}

	@RequestMapping("/update")
	@ResponseBody
	public ServerResponse<CartVo> update(HttpSession session, Integer productId, int count) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

		}
		return cartService.update(user.getId(), productId, count);
	}

	/*
	*
	* 删除购物车中的商品的方法
	*
	* */
	@RequestMapping("/delete")
	@ResponseBody
	public ServerResponse<CartVo> deleteProduct(HttpSession session, String productIds) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

		}
		return cartService.deleteProduct(user.getId(), productIds);
	}

	//查询购物车的接口
	@RequestMapping("/list")
	@ResponseBody
	public ServerResponse<CartVo> list(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

		}
		return cartService.listProduct(user.getId());
	}

	//全选商品的接口
	@RequestMapping("/select_all")
	@ResponseBody
	public ServerResponse<CartVo> selectAll(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

		}
		return cartService.selectOrUnselect(user.getId(),null,Const.Cart.CHECKED);
	}

	//全反选商品的接口
	@RequestMapping("/unSelect_all")
	@ResponseBody
	public ServerResponse<CartVo> unSelectAll(HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

		}
		return cartService.selectOrUnselect(user.getId(),null,Const.Cart.UN_CHECKED);
	}
	/*
	*
	* 单独选择的接口方法
	* 首先将全选置为不可用：将数据库中的商品的是否已经勾选置为零
	*
	* */
	@RequestMapping("/selectAlone")
	@ResponseBody
	public ServerResponse<CartVo> selectAlone(HttpSession session,Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		//在购物车单选反选是必须要productId的否则提示错误信息
		if(productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		}
		return cartService.selectOrUnselect(user.getId(),productId,Const.Cart.CHECKED);

	}
	//单独反选的接口方法：首先将全选置为可用
	@RequestMapping("/unSelectAlone")
	@ResponseBody
	public ServerResponse<CartVo> unSelectAlone(HttpSession session,Integer productId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if(productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		}
		return cartService.selectOrUnselect(user.getId(),productId,Const.Cart.UN_CHECKED);

	}
	//获得购物车中的商品数量的接口
	@RequestMapping("/getProudctCountInCart")
	@ResponseBody
	public ServerResponse<Integer> getProudctCountInCart(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createBySuccess(0);
		}
		return cartService.selectProductCountInCart(user.getId());

	}
}
