package com.listore.controller.backStage;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
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
	   @RequestMapping(value="/get_category",method = RequestMethod.POST)
	   @ResponseBody
	   public ServerResponse<List<Category>> getCategories(HttpSession session,String name){
		   //通过session中的对象获得当前登录的对象
		   User user = (User)session.getAttribute(Const.CURRENT_USER);
		   if(user == null){
			   return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录请登录");
		   }
		   //检查是否为管理员登录如果不是管理员登录的话阻止其进行操作
		   if(iUserService.check_admin_role(user).isSuccess()){
			   //说明为管理员登录
			   return categoryServiceImpl.getCategory(name);
		   }else{
			   return ServerResponse.createByErrorMessage("请以管理员身份登录");
		   }
		   
		  
	   }
	   //增加品类
	   @RequestMapping(value="/add_category",method=RequestMethod.POST)
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
	   @RequestMapping(value="/update_category",method=RequestMethod.POST)
	   @ResponseBody
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
	   @RequestMapping(value="/get_category_tree",method=RequestMethod.POST)
	   @ResponseBody
	   public ServerResponse getAllCategory(HttpSession session,@RequestParam(value="categoryId",defaultValue="0")int categoryId){
		   System.out.println("categoryId is " + categoryId);
		    User u = (User)session.getAttribute(Const.CURRENT_USER);
		    if(u == null){
		    	return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录！");
		    }
		    if(iUserService.check_admin_role(u).isSuccess()){
				   //如果是admin的话进行品类的添加
				   return categoryServiceImpl.getThisCategoryChildCategories(categoryId);
			   }else{
				   return ServerResponse.createByErrorMessage("请以管理员的身份登录");
			   }
	   }
	   
}
