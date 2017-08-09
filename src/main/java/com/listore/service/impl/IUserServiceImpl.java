package com.listore.service.impl;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.listore.commen.Const;
import com.listore.commen.ServerResponse;
import com.listore.commen.TokenCache;
import com.listore.dao.UserMapper;
import com.listore.pojo.User;
import com.listore.service.IUserService;
import com.listore.util.MD5Util;

@Service("iUserService")
public class IUserServiceImpl implements IUserService{
	//ͨ��ɨ�������ʽ���д���
	@Autowired
	private UserMapper userMapper;
	//����һ��������Ӧ����
	public ServerResponse<User> login(String username, String password){
		//检查用户名是否存在
		System.out.println("userMapper is " + userMapper);
		int resultCount = userMapper.checkUsername(username);
		System.out.println(resultCount);
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//对传入的密码进行加密
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if(user == null){
			return ServerResponse.createByErrorMessage("用户密码错误");
		}
		//将密码设置为空防止密码泄露
		user.setPassword(StringUtils.EMPTY);
		System.out.println(ServerResponse.createBySuccess("登录成功", user).getMsg());
		return ServerResponse.createBySuccess("用户登录成功",user);
	}
	//用户注册
	public ServerResponse<String> reigster(User user) {
		System.out.println("username is " + user.getUsername());
		
		//检查用户名的合法性
		ServerResponse<String> validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
		if(!validResponse.isSuccess()){
			//如果验证不成功的话返回验证不成功的信息
			return validResponse;
		}
		//检查邮箱的合法性
		validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
		if(!validResponse.isSuccess()){
			//如果邮箱验证不成功的话返回验证不成功的信息
			return validResponse;
		}
		//设置用户的角色
		user.setRole(Const.Role.ROLE_CUSTOM);
		//设置用户的密码为MD5加密后的密码
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		//插入一个用户如果失败有可能是db方面的错误
		int resultUser = userMapper.insert(user);
		if(resultUser == 0){
			return ServerResponse.createByErrorMessage("注册失败");
		}
		return ServerResponse.createBySuccessMsg("注册成功");
	}
	//����У��ķ���
	public ServerResponse<String> checkValid(String str, String type) {
		//���type��λ�յĻ���type����email���û�������������֤
		if(StringUtils.isNotBlank(type)){
			//��ʼУ��
			//检查用户名
			if(Const.USERNAME.equals(type)){
				 int resultCount = userMapper.checkUsername(str);
				 System.out.println("resultCount is " + resultCount);
				 if(resultCount > 0){
					 return ServerResponse.createByErrorMessage("该用户名已经备注册");
				 }
			}
			//检查邮箱
			if(Const.EMAIL.equals(type)){
				int resultCount = userMapper.checkEmail(str);
				if(resultCount > 0){
					return ServerResponse.createByErrorMessage("该邮箱已经被注册，请使用其他邮箱");
				}
			}
		}else{
			return ServerResponse.createByErrorMessage("参数错误");
		}
		return ServerResponse.createBySuccessMsg("验证成功");
	}
	//��������ʱ ��ȡ������ʾ����
	public ServerResponse<String> forgetGetQuestion(String username) {
		//У���û����Ƿ����
		ServerResponse response = this.checkValid(username,Const.USERNAME);
		if(response.isSuccess()){
			//如果返回成功的话说明之前没有注册，不具有密码找回的条件
			return ServerResponse.createByErrorMessage("user has not register");
		}
		//����˵���û��Ǵ��ڵ�Ȼ��ͨ���û����ҵ������һ���ʾ����
		String question = userMapper.selectQuestionByUser(username);
		//��������Ƿ�Ϊ��
		if(StringUtils.isNotBlank(question)){
			//˵�����ⲻ�ǿյ�
			return ServerResponse.createBySuccessMsg(question);
		}else{
			return ServerResponse.createByErrorMessage("问题为空");
		}
		
	}
	//检查问题答案是否正确
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int answerCount = userMapper.checkAnswer(username,question,answer);
		if(answerCount > 0){
			//说明问题答案是正确的 生成一个不可重复的字符串
			String forgetToken = UUID.randomUUID().toString();
			System.out.println(Preconditions.class.getProtectionDomain()
					.getCodeSource().getLocation().toExternalForm());
			//将每个用户对应的特定的用户码放到缓存中
			TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,forgetToken);
			//将token存入到缓存中
			return ServerResponse.createBySuccess(forgetToken);
		}
		//输出错误信息
		return ServerResponse.createByErrorMessage("问题的答案错误");
	}
	//��������
	public ServerResponse<String> ResetPassword(String username, String passwordNew, String forgetToken) {
		
		// TODO Auto-generated method stub
		//У��
		if(StringUtils.isBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("token Ϊ��");
		}
		//У���û����Ƿ����
		ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
		if(validResponse.isSuccess()){
			return ServerResponse.createByErrorMessage("用户名错误");
		}
		String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
		if(StringUtils.isBlank(token)){
			return ServerResponse.createByErrorMessage("token为空");
					
		}
		if(StringUtils.equals(forgetToken,token)){
			//����������м���
			String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
			//��������и���
			int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
			if(rowCount > 0){
			return ServerResponse.createBySuccessMsg("修改密码成功");
			}else{
				return ServerResponse.createByErrorMessage("修改密码失败");
			}
			
		}else{
			return ServerResponse.createByErrorMessage("token不一致");
		}
	}
	//��¼״̬����������
	public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
		//��ֹ����ԽȨ��ҪУ��һ������û��ľ����룬һ��Ҫָ��������û�����Ϊ���ǻ��ѯһ��count��1�������ָ��id��ô�ͻ᷵��TRUE��
		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("旧密码错误");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updataCount = userMapper.updateByPrimaryKeySelective(user);
		if(updataCount > 0){
			//�����������³ɹ�
			return ServerResponse.createBySuccessMsg("重置密码成功");
		}
		return ServerResponse.createByErrorMessage("重置密码失败");
	}
	//�����û���Ϣ
	public ServerResponse<User> update_userInfo(User newUser) {
		// TODO Auto-generated method stub
		//username�ǲ��ܱ����µ�
		//emailҲҪ����һ��У�飬У���µ�email�ǲ����Ѿ����ڣ����Ҵ��ڵ�email�����ͬ�Ļ������������ǵ�������û���
		int resultCount = userMapper.checkEmailByUserId(newUser.getEmail(),newUser.getId());
		//���resultCount > 0�Ļ�˵���������Ѿ�������ע�����
		if(resultCount > 0){
			return ServerResponse.createByErrorMessage("�������Ѿ���ռ��");
		}
		User updateUser = new User();
		updateUser.setId(newUser.getId());
		updateUser.setEmail(newUser.getEmail());
		updateUser.setPhone(newUser.getPhone());
		updateUser.setQuestion(newUser.getQuestion());
		updateUser.setAnswer(newUser.getAnswer());
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount > 0){
			return ServerResponse.createBySuccess("���¸�����Ϣ�ɹ�",updateUser);
		}
		return ServerResponse.createByErrorMessage("���¸�����Ϣʧ��");
	}
	//��ȡ�����û���Ϣ
	public ServerResponse<User> getUserInfo(int id) {
		// TODO Auto-generated method stub
		User user = userMapper.selectByPrimaryKey(id);
		System.out.println("user is " + user);
		if(user == null){
			return ServerResponse.createByErrorMessage("用户没有登录");
		}
		ServerResponse<User> response = ServerResponse.createBySuccess(user);
		return response;
	}
}
