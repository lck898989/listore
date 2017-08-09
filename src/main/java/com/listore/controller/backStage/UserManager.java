package com.listore.controller.backStage;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.Const;
import com.listore.commen.ServerResponse;
import com.listore.pojo.User;
import com.listore.service.IUserService;

/*
 * 
 * 后台管理
 * */
@Controller
@RequestMapping("/manager/user")
public class UserManager {
	@Resource(name="iUserService")
	private IUserService iUserService;
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(@Param("username")String username,@Param("password")String password,HttpSession session){
	   ServerResponse<User> response = iUserService.login(username, password);
	   if(response.isSuccess()){
		   //获得当前登录的用户
		   User user = response.getData();
		   if(user.getRole() == Const.Role.ROLE_ADMIN){
			   //说明登录的是管理员将用户装进session中去
			   session.setAttribute(Const.CURRENT_USER,user);
			   return response;
			  
		   }else{
		    return ServerResponse.createByErrorMessage("不是管理员身份");
		   }
		}
	   return response;
	}

}
