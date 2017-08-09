package com.listore.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
import com.listore.pojo.Category;
import com.listore.service.ICategoryService;

@Service("categoryServiceImpl")
public class ICategoryServiceImpl implements ICategoryService{
	//访问数据层的对象
	@Resource
	private CategoryMapper categoryMapper;
	@Override
	public ServerResponse<List<Category>> getCategory(String name) {
		//用于存放子节点的容器
		List<Category> categories = new ArrayList<Category>();
		// TODO Auto-generated method stub
		//检查类别名是否存在
		int nameCount = categoryMapper.checkName(name);
		if(nameCount == 0){
			return ServerResponse.createByErrorMessage("该类别名不存在");
		}
		//说明类别名存在
		Category c = categoryMapper.selectByName(name);
		//获得该品类的id号
		int categoryId = c.getId();
		//凡是表中pid == id 的记录都是该节点的子节点
		
		return null;
	}

	@Override
	public boolean addCategory() {
		// TODO Auto-generated method stub
		return false;
	}

}
