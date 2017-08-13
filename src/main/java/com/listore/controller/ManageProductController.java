package com.listore.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;
import com.listore.pojo.User;
import com.listore.service.IProductService;
import com.listore.service.IUserService;

/*
 * 后台管理商品控制器
 * 
 * */
@Controller
@RequestMapping("manager/product")
public class ManageProductController {
	 @Resource(name="productServer")
	 private IProductService productServer;
	 //判断用户是否为管理员
	 @Resource(name="iUserService")
	 private IUserService userServer;
	 /*
	  * 插入商品或者更新商品
	  * 
	  * 
	  * */
	 @RequestMapping(value="/saveOrUpdate_product",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse saveOrUpdateProduct(HttpSession session,Product product){
		 //检查用户是否登录
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
	      if(user == null){
	    	  return ServerResponse.createByErrorMessage("您还未登录，请先登录");
	      }
	    	  //检查是否是以管理员身份登录
	    	  if(userServer.check_admin_role(user).isSuccess()){
	    		  return productServer.saveOrUpdateProduct(product);
	    	  }else{
	    		  return ServerResponse.createByErrorMessage("请以管理员身份登录");
	           }
	 }
	 /*
	  * 
	  * 商品列表
	  * 
	  * 
	  * */
	 //获得所有商品的信息
	 @RequestMapping(value="/get_products",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<List<Product>> getProducts(HttpSession session){
		      //获得session中的信息看是否已经登录
		      User user = (User)session.getAttribute(Const.CURRENT_USER);
		      if(user == null){
		    	  return ServerResponse.createByErrorMessage("您还未登录，请先登录");
		      }else{
		    	  //检查是否是以管理员身份登录
		    	  if(userServer.check_admin_role(user).isSuccess()){
		    		  return productServer.getProducts();
		    	  }else{
		    		  return ServerResponse.createByErrorMessage("请以管理员身份登录");
		           }
		      }
	 }
	 /*
	  * 改变商品销售状态
	  * 
	  * */
	 @RequestMapping(value="/set_saleStatus",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<String> setSaleStatus(HttpSession session,@RequestParam(value = "productId",required=true)int productId,@RequestParam(value="status",required=true)int status){
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.setSaleStatus(productId,status);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
	 }
	 /*
	  * 获取产品详情方法定义
	  * 
	  * */
	 @RequestMapping(value="/get_product_details",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<Product> getProductDetails(HttpSession session,Integer productId){
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.getProductDetails(productId);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
		 
	 }
	 /*
	  * 商品搜索
	  * 
	  * */
	/* public ServerResponse<List<Product>> searchProducts(){
		 
	 }*/

}
