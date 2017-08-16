package com.listore.service;

import java.util.List;

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
	ServerResponse<List<Product>> getProductList(int pageNum,int pageSize);

}
