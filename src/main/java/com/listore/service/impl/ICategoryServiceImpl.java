package com.listore.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
import com.listore.pojo.Category;
import com.listore.pojo.User;
import com.listore.service.ICategoryService;

@Service(value="categoryServiceImpl")
public class ICategoryServiceImpl implements ICategoryService{
	//访问数据层的对象
	@Resource
	private CategoryMapper categoryMapper;
	@Override
	public ServerResponse<List<Category>> getCategory(String name) {
		// TODO Auto-generated method stub
		ServerResponse<List<Category>> categories = null;
		//检查类别名是否存在
		int nameCount = categoryMapper.checkName(name);
		if(nameCount == 0){
			return ServerResponse.createByErrorMessage("该类别名不存在");
		}
		//说明类别名存在
		Category c = categoryMapper.selectByName(name);
		//获得该品类的id号
		int categoryId = c.getId();
		System.out.println("in service categoryId is " + categoryId);
		//凡是表中pid == id 的记录都是该节点的子节点
		categories = categoryMapper.selectCategoriesByPid(categoryId);
	    Method m = SqlSession.class.getMethods()[0];
	    System.out.println("m is " + m);
	    Field[] f = SqlSession.class.getDeclaredFields();
	    System.out.println(f);
		if(categories != null){
			categories.getStatus();
			categories.getMsg();
			categories.getData();
			return  categories;	
		}else{
			return ServerResponse.createBySuccess("提取子品类失败",null);
		}
		
	}

	@Override
	public ServerResponse<String> addCategory(Category c) {
		// TODO Auto-generated method stub
		       //检查品类的名字是否与数据库中的名字重复
				if(null != c.getParentId() || !StringUtils.isBlank(c.getName())){
					if(StringUtils.isBlank(String.valueOf(c.getParentId()))){
						c.setParentId(0);
					}
					//用户名在数据库只不存在的话说明可以插入该条记录
					if(categoryMapper.check_add_Category(c.getName()) == 0){
						int insertCount = categoryMapper.insertSelective(c);
						if(insertCount > 0){
							return ServerResponse.createBySuccess("添加成功");
						}
						return ServerResponse.createByErrorMessage("插入数据时候出错");
					}
					return ServerResponse.createByErrorMessage("该品类已经存在");
				}
				return ServerResponse.createByErrorMessage("参数错误！品类名为空");
			}
	//更新品类信息
	public ServerResponse<String> updateCategory(Category c){
		  //检查品类ID是否为空，如果为空的话返回错误信息，因为跟新一条记录首先要找到它的主键
		  if(c.getId() == null || StringUtils.isBlank(c.getName())){
			  return ServerResponse.createByErrorMessage("品类的ID为空或者参数错误");
		  }else{
			  int updateCount =  categoryMapper.updateByPrimaryKeySelective(c);
			  if(updateCount > 0){
				  return ServerResponse.createBySuccessMsg("更新成功");
			  }else{
				  return ServerResponse.createByErrorMessage("更新失败");
			  }
		  }
	}

}
