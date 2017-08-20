package com.listore.service.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
import com.listore.pojo.Category;
import com.listore.service.ICategoryService;
/*
 * 控制层
 *  |
 *  |
 * 服务层
 *  |
 *  |
 * 数据访问层
 * 
 * */
@Service(value="categoryServiceImpl")
public class ICategoryServiceImpl implements ICategoryService{
	//创建数据访问层的对象
	@Resource
	private CategoryMapper categoryMapper;
	@Override
	public ServerResponse<List<Category>> getCategory(String name) {
		// TODO Auto-generated method stub
		List<Category> categories = null;
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
		if(categories != null){
			 //将装有category的对象装入ServerResponse中
			return  ServerResponse.createBySuccess("got them",categories);	
		}else{
			return ServerResponse.createByErrorMessage("提取平级子类别失败");
		}
		
	}
    //添加产品信息
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
   //获得该节点的所有子节点
	public ServerResponse<List<Integer>> getThisCategoryChildCategories(int categoryId){
		 Set<Category> categorySet = Sets.newHashSet();
		 findDeepCategoryById(categorySet,categoryId);
		 //这时候categroySet已经被填充完毕了
		 if(categorySet != null){
			 List<Integer> categoryIdList = Lists.newArrayList();
			 for(Category c : categorySet){
				 categoryIdList.add(c.getId());
				 System.out.println("id is " + c.getId());
			 }
			 return ServerResponse.createBySuccess(categoryIdList);
		 }
		 return ServerResponse.createByErrorMessage("提取失败");
		 
	}
	//递归查找该节点的全部子节点
	private Set<Category> findDeepCategoryById(Set<Category> categorySet,int categoryId){
		  //System.out.println("in findDeepCategoryById categoryId is " + categoryId);
		 //依据id从数据库中获得相应的对象 当传进来的ID为零的时候获得的category为null
		 Category c = categoryMapper.selectByPrimaryKey(categoryId);
		// System.out.println("c is " + c);
		 if(c != null){
			 //将该类别加入到类别集合中
			 categorySet.add(c);
		 }
		 //接下来创建一个category集合 用来装配 该ID的子节点
		 List<Category> categoryList = categoryMapper.selectCategoriesByPid(categoryId);
		 //遍历该节点结合中的元素看该节点下是否有孩子节点 该处会返回一个空的集合不会返回一个空指针
		 for(Category categoryItem : categoryList){
			 //继续查找该节点是否有孩子节点进行递归调用
			 findDeepCategoryById(categorySet,categoryItem.getId());
		 }
		 return categorySet;
		 
	}
}
