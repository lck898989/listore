package com.listore.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;
import com.listore.vo.ProductDetailVo;

/*
 * 产品服务层的接口方法定义
 * 
 * */
public interface IProductService {
	ServerResponse<List<Product>> getProducts();
	ServerResponse saveOrUpdateProduct(Product product);
	ServerResponse<String> setSaleStatus(int productId,int status);
	ServerResponse<ProductDetailVo> getProductDetailVos(Integer productId);
	ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);
	ServerResponse<PageInfo> searchProductByNameAndId(String productName, Integer productId,int pageNum,int pageSize);
	ServerResponse<ProductDetailVo> productDetail(Integer productId);
	ServerResponse<PageInfo> searchProductByNameAndCategoryIds(String productName,Integer categoryId, int pageNum, int pageSize,String orderBy);

}
