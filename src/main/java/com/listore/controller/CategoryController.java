package com.listore.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Category;
import com.listore.pojo.User;
import com.listore.service.ICategoryService;
import com.listore.service.IUserService;

/*
 * 商品品类控制器
 * 
 * */
@Controller
@RequestMapping("/manager/category")
public class CategoryController {
	   @Resource(name="iUserService")
	   private IUserService iUserService;
	   @Resource(name="categoryServiceImpl")
       private ICategoryService categoryServiceImpl;
	   //获取品类的子节点
	   @RequestMapping("/get_category")
	   @ResponseBody
	   public ServerResponse<List<Category>> getCategories(String name){
		  return categoryServiceImpl.getCategory(name);
	   }
	   //增加品类
	   @RequestMapping("/add_category")
	   @ResponseBody
	  public ServerResponse<String> addCategory(HttpSession session,Category c){
		   //获得在session中的用户
		   User u = (User)session.getAttribute(Const.CURRENT_USER);
		   if(u == null){
			   return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录！");
		   }
		  
		   if(iUserService.check_admin_role(u).isSuccess() && c != null){
			   //如果是admin的话进行品类的添加
			   return categoryServiceImpl.addCategory(c);
		   }else{
			   return ServerResponse.createByErrorMessage("请以管理员的身份登录");
		   }
		   
	   }
	   //更新产品类名字
	   public ServerResponse<String> updateCategory(HttpSession session,Category c){
		    User u = (User)session.getAttribute(Const.CURRENT_USER);
		    if(u == null){
		    	return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录！");
		    }
		    if(iUserService.check_admin_role(u).isSuccess() && c != null){
				   //如果是admin的话进行品类的添加
				   return categoryServiceImpl.updateCategory(c);
			   }else{
				   return ServerResponse.createByErrorMessage("请以管理员的身份登录");
			   }
	   }
	   
}
