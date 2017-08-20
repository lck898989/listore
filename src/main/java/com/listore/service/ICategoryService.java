package com.listore.service;

import java.util.List;

import com.listore.commen.ServerResponse;
import com.listore.pojo.Category;

/*
 * 商品品类管理接口
 * 1：获取品类子节点
 * 2：增加品类节点
 * 3：修改类别名称
 * 4：获取当前分类ID及递归子节点的categoryid
 * */
public interface ICategoryService {
          //获取品类子节点（评级）
	     ServerResponse<List<Category>> getCategory(String name);
	     //增加节点
		ServerResponse<String> addCategory(Category c);
		//更新品类信息
		ServerResponse<String> updateCategory(Category c);
		ServerResponse<List<Integer>> getThisCategoryChildCategories(int categoryId);
}
