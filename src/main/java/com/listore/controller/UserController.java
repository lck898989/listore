package com.listore.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.User;
import com.listore.service.IUserService;
import static com.google.common.base.Preconditions.checkArgument;

@Controller
@RequestMapping("/user")
public class UserController{
	@Resource(name="iUserService")
	private IUserService iUserService;
	
	/*
	 * 登录页面
	 */
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody       //返回值会自动转换为json对象
	public ServerResponse<User> login(String username,String password,HttpSession session){
		//service
		System.out.println("username is " + username);
		
		checkArgument(true);
		
		System.out.println("Got it!!!");
		
		ServerResponse<User> serverResponse = iUserService.login(username, password);
		System.out.println(serverResponse.isSuccess());
		//如果返回结果的信息为真的话说明登录成功
		if(serverResponse.isSuccess()){
			 session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
			 System.out.println("login is success");
		}
		return serverResponse;
	}
	
	@RequestMapping(value="/logout",method=RequestMethod.POST)
	@ResponseBody
	//退出登录功能
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
	    return	ServerResponse.createBySuccess();
	}
	@RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	//注册功能
	public ServerResponse<String> register(User user){
		return iUserService.reigster(user);
	}
	@RequestMapping(value="/checkValid",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
		return iUserService.checkValid(str,type);
	}
	/*获得用户信息*/
	@RequestMapping(value="/getUserInfo",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		System.out.println("session is " + session);
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user != null){
			 return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByErrorMessage("您还没有登录,请登录！");
	}
	/*忘记密码找回答案*/
	@RequestMapping(value="/forget_get_question",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		return iUserService.forgetGetQuestion(username);
	}
	
	/*
	 * 忘记密码检查答案
	 * 
	 * */
	@RequestMapping(value="/forget_check_answer",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		// FIXME: remove println later
		System.out.println("get parameter username = " + username + " question = " + question + " answer = " + answer);
		
		return iUserService.checkAnswer(username,question,answer);
	}
	//重设密码
	@RequestMapping(value="/forget_reset_password",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
		return iUserService.ResetPassword(username,passwordNew,forgetToken);
	}
	//登录状态重新设置密码
	@RequestMapping(value="/reset_password",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorMessage("请先登录！");
		}
		return iUserService.resetPassword(passwordOld, passwordNew,user);
	}
	//更新用户信息
	@RequestMapping(value="/update_userInfo",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_userInfo(HttpSession session,User newUser){
		User oldUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(oldUser == null){
			return ServerResponse.createByErrorMessage("用户信息为空");
		}
		//设置新创建用户的id和用户名信息
	     newUser.setId(oldUser.getId());
	     newUser.setUsername(oldUser.getUsername());
	     ServerResponse<User> response = iUserService.update_userInfo(newUser);
	     if(response.isSuccess()){
	    	 session.setAttribute(Const.CURRENT_USER,response.getData());
	    	
	     }
	     
		  return response;
	}
	//获得登录用户的信息
	@RequestMapping(value="/get_User_Info",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_User_Info(HttpSession session){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录以后重试");
		}
		return iUserService.getUserInfo(currentUser.getId());
	}
}
