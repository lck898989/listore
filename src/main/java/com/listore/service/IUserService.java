package com.listore.service;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;

import com.listore.commen.ServerResponse;
import com.listore.pojo.User;

public interface IUserService {
	 	//�û���¼
        public ServerResponse login(String username,String password);
        //�û�ע��
        public ServerResponse<String> reigster(User user);
		public ServerResponse<String> checkValid(String str, String type);
		public ServerResponse<String> forgetGetQuestion(String username);
		public ServerResponse<String> checkAnswer(String username, String question, String answer);
		public ServerResponse<String> ResetPassword(String username, String passwordNew, String forgetToken);
		public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);
		public ServerResponse<User> update_userInfo(User newUser);
		public ServerResponse<User> getUserInfo(int userId);
}
