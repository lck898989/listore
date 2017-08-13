package com.listore.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
import com.listore.dao.ProductMapper;
import com.listore.pojo.Category;
import com.listore.pojo.Product;
import com.listore.service.IProductService;
import com.listore.util.PropertiesUtil;
import com.listore.vo.ProductDetail;
@Service("productServer")
public class IProductServiceImpl implements IProductService {
	   @Resource
       private ProductMapper productMapper;
	   @Resource
	   private CategoryMapper categoryMapper;
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
			 return ServerResponse.createBySuccessMsg("产品参数错误");
		}
		//设置产品的销售状态
		public ServerResponse<String> setSaleStatus(int productId,int status){
			if(StringUtils.isBlank(String.valueOf(productId)) || StringUtils.isBlank(String.valueOf(status))){
				return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());			
			}
			//第一种方法进行更新销售状态
			int  updateRow = productMapper.updateSaleStatusById(productId, status);
			/*
			 * 第二种方法更新销售状态
			 * Product product = new Product();
			 * product.setId(productId);
			 * product.setStatus(status);
			 * int updateRow = productMapper.updateByPrimaryKeySelective(product);
			 * */
			
			if( updateRow> 0){
				return ServerResponse.createBySuccessMsg("更新销售状态成功");
			}
			return ServerResponse.createByErrorMessage("更新销售状态失败");
		}
		//获得产品信息详情
		@Override
		public ServerResponse<Object> getProductDetails(Integer productId) {
			if(productId == null){
				return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
			}
			Product product = productMapper.selectByPrimaryKey(productId);
			//返回一个VO 对象
			if(product != null){
				ProductDetail productDetails = assembleProductDetails(product);
				return ServerResponse.createBySuccess(productDetails);
			}
			return ServerResponse.createByErrorMessage("获取产品详情失败");
		}
		//将一个pojo对象装换为一个ＶＯ对象
		private ProductDetail assembleProductDetails(Product product){
			ProductDetail productDetail = new ProductDetail();
			productDetail.setId(product.getId());
			productDetail.setCategoryId(product.getCategoryId());
			productDetail.setDetail(product.getDetail());
			productDetail.setName(product.getName());
			productDetail.setMainImange(product.getMainImage());
			productDetail.setSubtitle(product.getSubImages());
			productDetail.setStatus(product.getStatus());
			productDetail.setStock(product.getStock());
			
			
			//设置imageHost:通过读取配置文件的内容进行对图片的地址进行配置防止硬编码  创建一个propertiesUtil处理配置文件
			productDetail.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://www.li.com/"));
			//设置createTime由于从数据库拿来的数据经过Product类的时候变成了毫秒数所以需要加工成时间类型
			
			
			//设置updateTime
			
			//设置parentCategoryId
			Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
			if(category == null){
				productDetail.setParentCategoryId(0);
			}else{
				productDetail.setParentCategoryId(category.getParentId());
			}
			
			return null;
		}

}
