package com.listore.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.CategoryMapper;
import com.listore.dao.ProductMapper;
import com.listore.pojo.Category;
import com.listore.pojo.Product;
import com.listore.service.ICategoryService;
import com.listore.service.IProductService;
import com.listore.util.PropertiesUtil;
import com.listore.util.TimeUtil;
import com.listore.vo.ProductDetailVo;
import com.listore.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
@Service("productServer")
public class IProductServiceImpl implements IProductService {
	   @Resource
       private ProductMapper productMapper;
	   @Resource
	   private CategoryMapper categoryMapper;
	   //获取categoryId的服务
	   @Resource(name="categoryServiceImpl")
	   private ICategoryService iCategoryService;
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
		public ServerResponse<ProductDetailVo> getProductDetailVos(Integer productId) {
			if(productId == null){
				return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
			}
			Product product = productMapper.selectByPrimaryKey(productId);
			//返回一个VO 对象
			if(product != null){
				ProductDetailVo productDetailVos = assembleproductDetailVos(product);
				return ServerResponse.createBySuccess(productDetailVos);
			}
			return ServerResponse.createByErrorMessage("获取产品详情失败");
		}
		//将一个pojo对象装换为一个ＶＯ对象
		private ProductDetailVo assembleproductDetailVos(Product product){
			ProductDetailVo productDetailVo = new ProductDetailVo();
			productDetailVo.setId(product.getId());
			productDetailVo.setCategoryId(product.getCategoryId());
			productDetailVo.setDetail(product.getDetail());
			productDetailVo.setName(product.getName());
			productDetailVo.setMainImange(product.getMainImage());
			productDetailVo.setSubtitle(product.getSubImages());
			productDetailVo.setStatus(product.getStatus());
			productDetailVo.setStock(product.getStock());
			
			
			//设置imageHost:通过读取配置文件的内容进行对图片的地址进行配置防止硬编码  创建一个propertiesUtil处理配置文件
			productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://www.li.com/"));
			//设置createTime由于从数据库拿来的数据经过Product类的时候变成了毫秒数所以需要加工成时间类型
			productDetailVo.setCreateTime(TimeUtil.dateToStr(product.getCreateTime()));
			//设置updateTime
            productDetailVo.setUpdateTime(TimeUtil.dateToStr(product.getUpdateTime()));			
			//设置parentCategoryId
			Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
			if(category == null){
				productDetailVo.setParentCategoryId(0);
			}else{
				productDetailVo.setParentCategoryId(category.getParentId());
			}
			
			return productDetailVo;
		}
		/*
		 * 获得产品列表  完成一个分页的功能
		 * 
		 * */
		public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
			//产品列表有时候并不需要把所有的产品属性都列举出来这时候就需要一个中间的数据对象进行加工这里使用Value Object进行封装
			/*
			 * 利用PageHelper进行分页的实现顺序
			 * 1：startPage -- start
			 * 2:填充自己的sql查询逻辑
			 * 3：pageHelper收尾
			 * */
			//利用PageHelper类进行分页功能的实现
			PageHelper.startPage(pageNum, pageSize);
			//填充自己的SQL语句
			List<Product> productList = productMapper.select();
			if(productList == null){
				return ServerResponse.createByErrorMessage("查询错误");
			}
			if(productList != null){
				//将对象进行组装使其成为VO对象
				List<ProductListVo> productVos = Lists.newArrayList();
				for(Product productItem:productList){
					//将对象进行组装
					 ProductListVo plv = assembleProductListVo(productItem);
					 productVos.add(plv);
				}
				PageInfo pageResult = new PageInfo(productList);
				pageResult.setList(productVos);
				return ServerResponse.createBySuccess(pageResult);
			}
			return ServerResponse.createByErrorMessage("产品列表错误");
		}
		//ProductListVo 组装方法
		private ProductListVo assembleProductListVo(Product product){
			ProductListVo productListVo = new ProductListVo();
			productListVo.setId(product.getId());
			productListVo.setCategoryId(product.getCategoryId());
			productListVo.setMainImage(product.getMainImage());
			productListVo.setName(product.getName());
			productListVo.setPrice(product.getPrice());
			productListVo.setStatus(product.getStatus());
			productListVo.setSubtitle(product.getSubtitle());
			productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://www.li.com/"));
			return productListVo;
		}
		@Override
		public ServerResponse<PageInfo> searchProductByNameAndId(String productName, Integer productId,int pageNum,int pageSize) {
			PageHelper.startPage(pageNum, pageSize);
			//加上%表示模糊查询的方式进行查询
			if(StringUtils.isNotBlank(productName)){
				productName = new StringBuilder().append("%").append(productName).append("%").toString();
			}
			System.out.println("productName is " + productName);
			List<Product> productList = Lists.newArrayList();
			System.out.println("productMapper is " + productMapper);
			productList = productMapper.selectByProductNameAndProductId(productName,productId);
			List<ProductListVo> productVoList = Lists.newArrayList();
			if(productList != null){
				for(Product productListItem:productList){
					//将产品实体对象转化为VO对象
					ProductListVo plv = assembleProductListVo(productListItem);
					productVoList.add(plv);
				}
				PageInfo resultPage = new PageInfo(productList);
				resultPage.setList(productVoList);
				return ServerResponse.createBySuccess(resultPage);
			}
			return ServerResponse.createByErrorMessage("参数错误");
		}
		/*
		 * 
		 * 前台页面取得产品的细节
		 * 
		 * */
		@Override
		public ServerResponse<ProductDetailVo> productDetail(Integer productId) {
			if(productId == null){
				return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
			}
			Product product = productMapper.selectByPrimaryKey(productId);
			//返回一个VO 对象
			if(product != null){
				//将pojo对象转换为浏览器要显示的对象
				ProductDetailVo productDetailVos = assembleproductDetailVos(product);
				return ServerResponse.createBySuccess(productDetailVos);
			}
			if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
				return ServerResponse.createByErrorMessage("产品已经下架");
			}
			return ServerResponse.createByErrorMessage("获取产品详情失败");
		}
		/*
		 * 前台页面进行搜索产品的需要
		 * 
		 * */
		@Override
		public ServerResponse<PageInfo> searchProductByNameAndCategoryIds(String productName,Integer categoryId, int pageNum, int pageSize,String orderBy) {
			//判断名称是否为空
			if(StringUtils.isBlank(productName) && categoryId == null){
				return ServerResponse.createByErrorMessage("参数错误");
			}
			//用来盛放种类ID的容器
			List<Integer> categoryIdList = new ArrayList<Integer>();
			//单独判断cateogryId
			if(categoryId != null){
				//获得该对象
				Category category = categoryMapper.selectByPrimaryKey(categoryId);
				if(category == null && StringUtils.isBlank(productName)){
					//没有命中数据库中的内容，但是也要有分页的内容
					PageHelper.startPage(pageNum,pageSize);
					List<ProductListVo> pvo = Lists.newArrayList();
					PageInfo pageInfo = new PageInfo(pvo);
					return ServerResponse.createBySuccess(pageInfo);
				}
				//需要用到递归算法来查询所有子节点
				categoryIdList = iCategoryService.getThisCategoryChildCategories(categoryId).getData();
			}
			if(StringUtils.isNoneBlank(productName)){
				productName = new StringBuilder().append("%").append(productName).append("%").toString();

			}
			//开始分页
			PageHelper.startPage(pageNum,pageSize);
			//动态排序
			if(StringUtils.isNotBlank(orderBy)){
				if(Const.productOrder.productOrder.contains(orderBy)){
					String[] orderByArray = orderBy.split("_");
					//orderBy方法的格式为orderBy("price asc")
					PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
				}
			}
			//获得商品的列表 因为pagehelper.startPage()以后会寻找执行SQL语句的进程 找到之后他把List对象置为Page对象
			List<Product> productList = productMapper.selectByProductNameAndCategoryIds(StringUtils.isNotBlank(productName)? productName : null ,categoryIdList.size() == 0 ? null:categoryIdList);
            List<ProductListVo> productListVos = Lists.newArrayList();
			for(Product product:productList){
				ProductListVo productListVo = assembleProductListVo(product);
				productListVos.add(productListVo);
			}
			PageInfo pageInfo = new PageInfo(productList);
			pageInfo.setList(productListVos);
			return ServerResponse.createBySuccess(pageInfo);
		}
}
