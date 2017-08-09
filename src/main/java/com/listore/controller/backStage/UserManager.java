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
 * ��̨����
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
		   //��õ�ǰ��¼���û�
		   User user = response.getData();
		   if(user.getRole() == Const.Role.ROLE_ADMIN){
			   //˵����¼���ǹ���Ա���û�װ��session��ȥ
			   session.setAttribute(Const.CURRENT_USER,user);
			   return response;
			  
		   }else{
		    return ServerResponse.createByErrorMessage("���ǹ���Ա���");
		   }
		}
	   return response;
	}

}
