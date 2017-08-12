package com.listore.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.listore.commen.ServerResponse;
import com.listore.dao.ProductMapper;
import com.listore.pojo.Product;
import com.listore.service.IProductService;
@Service("productServer")
public class IProductServiceImpl implements IProductService {
	   @Resource
       private ProductMapper productMapper;
	    //商品列表
		@Override
		public ServerResponse<List<Product>> getProducts() {
			List<Product> products = Lists.newArrayList();
			products = productMapper.select();
			if(products != null){
				return ServerResponse.createBySuccess(products);
			}else{
				return ServerResponse.createBySuccessMsg(null);
			}
			
		}
		//保存新增商品
		public ServerResponse saveOrUpdateProduct(Product product){
			 if(product != null){
				 //将产品的子图的第一个图片赋给主图
				 if(StringUtils.isNoneBlank(product.getSubImages())){
					 String[] imagesArray = product.getSubImages().split(",");
					 if(imagesArray.length > 0)
					 product.setMainImage(imagesArray[0]);
				 }
				 //当产品的ID为空的时候说明是插入操作
				 if(product.getId() == null){
					 int insertCount = productMapper.insertSelective(product);
					 if(insertCount > 0){
						 System.out.println("id is " + product.getId());
						 System.out.println("create_time is and update_time is " + product.getCreateTime() + "--->" + product.getUpdateTime());
						 return ServerResponse.createBySuccess("插入成功");
					 }else{
						 return ServerResponse.createByErrorMessage("保存失败");
					 }
				 }else{
					  int updateCount = productMapper.updateByPrimaryKey(product);
					  if(updateCount > 0){
						  return ServerResponse.createBySuccessMsg("更新成功");
					  }
					  return ServerResponse.createByErrorMessage("更新失败");
					}
				
			 }
			 return ServerResponse.createBySuccessMsg("产品错误");
		}

}
