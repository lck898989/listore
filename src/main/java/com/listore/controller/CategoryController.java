package com.listore.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.listore.commen.ServerResponse;
import com.listore.pojo.Category;
import com.listore.service.ICategoryService;

/*
 * 商品品类控制器
 * 
 * */
@Controller
@RequestMapping("/manager/category")
public class CategoryController {
	   @Resource(name="categoryServiceImpl")
       private ICategoryService categoryServiceImpl;
	   //获取品类的子节点
	   @RequestMapping("/get_category")
	   @ResponseBody
	   public ServerResponse<List<Category>> getCategories(String name){
		  return categoryServiceImpl.getCategory(name);
	   }
	   
}
