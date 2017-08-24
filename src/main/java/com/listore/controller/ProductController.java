package com.listore.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.listore.commen.ServerResponse;
import com.listore.service.IProductService;
import com.listore.vo.ProductDetailVo;

/*
 * 
 * 前台的产品控制层
 * 
 * */
@Controller
@RequestMapping("/product")
public class ProductController {
	@Resource(name="productServer")
	private IProductService iProductService;
	
	/*
	 * 获得产品的详细信息
	 * */
	@RequestMapping("/productDetail")
	@ResponseBody
	public ServerResponse<ProductDetailVo> detailProduct(Integer productId){
		return iProductService.productDetail(productId);
	}
	/*
	 * 产品的搜索方法
	 * 包括 自动排序功能，
	 * 查询的时候要用到产品类别ID
	 * 如果查询到最顶上的id需要递归查询其子ID
	 * */
	@RequestMapping("/searchProduct")
	@ResponseBody
	public ServerResponse<PageInfo> searchProduct(@RequestParam(value="productName",required=false)String productName,
													@RequestParam(value="categoryId",required=false)Integer categoryId,
													@RequestParam(value="pageNum",defaultValue="1")int pageNum,
													@RequestParam(value="pageSize",defaultValue="10")int pageSize,
													@RequestParam(value="orderBy",defaultValue="")String orderBy){
		return iProductService.searchProductByNameAndCategoryIds(productName,categoryId, pageNum, pageSize,orderBy);
		
	}

}
