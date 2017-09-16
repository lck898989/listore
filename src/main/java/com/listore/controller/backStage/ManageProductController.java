package com.listore.controller.backStage;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.google.inject.internal.util.Maps;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Product;
import com.listore.pojo.User;
import com.listore.service.IFileService;
import com.listore.service.IProductService;
import com.listore.service.IUserService;
import com.listore.util.PropertiesUtil;
import com.listore.vo.ProductDetailVo;

/*
 * 后台管理商品控制器
 * 前端不能访问的原因查找：
 * 
 * */
@Controller
<<<<<<< HEAD
@RequestMapping("/manager/product")
=======
@RequestMapping("/manage/product")
>>>>>>> 468ef20602d48260cac85c26ca7098a16de44c7e
public class ManageProductController {
	 @Resource(name="productServer")
	 private IProductService productServer;
	 //判断用户是否为管理员
	 @Resource(name="iUserService")
	 private IUserService userServer;
	 @Resource(name="iFileService")
	 private IFileService iFileService;
	 /*
	  * 插入商品或者更新商品
	  * 
	  * 
	  * */
	 @RequestMapping(value="/saveOrUpdate_product",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse saveOrUpdateProduct(HttpSession session,Product product){
		 //检查用户是否登录
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
	      if(user == null){
	    	  return ServerResponse.createByErrorMessage("您还未登录，请先登录");
	      }
	    	  //检查是否是以管理员身份登录
	    	  if(userServer.check_admin_role(user).isSuccess()){
	    		  return productServer.saveOrUpdateProduct(product);
	    	  }else{
	    		  return ServerResponse.createByErrorMessage("请以管理员身份登录");
	           }
	 }
	 /*
	  * 
	  * 商品列表
	  * 
	  * 
	  * */
	 //获得所有商品的信息
	 @RequestMapping(value="/get_products",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<List<Product>> getProducts(HttpSession session){
		      //获得session中的信息看是否已经登录
		      User user = (User)session.getAttribute(Const.CURRENT_USER);
		      if(user == null){
		    	  return ServerResponse.createByErrorMessage("您还未登录，请先登录");
		      }else{
		    	  //检查是否是以管理员身份登录
		    	  if(userServer.check_admin_role(user).isSuccess()){
		    		  return productServer.getProducts();
		    	  }else{
		    		  return ServerResponse.createByErrorMessage("请以管理员身份登录");
		           }
		      }
	 }
	 /*
	  * 改变商品销售状态
	  * 
	  * */
	 @RequestMapping(value="/set_saleStatus",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<String> setSaleStatus(HttpSession session,@RequestParam(value = "productId",required=true)int productId,@RequestParam(value="status",required=true)int status){
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.setSaleStatus(productId,status);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
	 }
	 /*
	  * 获取产品详情方法定义
	  * 
	  * */
	 @RequestMapping(value="/get_product_details",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<ProductDetailVo> getProductDetails(HttpSession session,Integer productId){
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.getProductDetailVos(productId);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
		 
	 }
	 /*
	  * 获得产品列表
	  * 将RequestParam中的pageNum设置为默认的1
	  * 将RequestParam中的pageSize设置为默认的10条记录
	  * 
	  * */
	 @RequestMapping(value="/get_product_list",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse<PageInfo> getProductList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1") int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
		 //检查用户登录状态
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.getProductList(pageNum,pageSize);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
		 
	 }
	 /*
	  * 商品搜索
	  * 
	  * */
	 @RequestMapping(value="/searchProduct")
	 @ResponseBody
	 public ServerResponse<PageInfo> searchProduct(HttpSession session,String productName,Integer productId,@RequestParam(value="pageNum",defaultValue="1") int pageNum,@RequestParam(value="pageSize",defaultValue="10") int pageSize){
		 //检查用户登录状态
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			 return  productServer.searchProductByNameAndId(productName,productId,pageNum,pageSize);
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
		 
	 }
	 /*
	  * 文件上传
	  * 
	  * */
	 @RequestMapping(value="/uploadFile",method=RequestMethod.POST)
	 @ResponseBody
	 public ServerResponse uploadFile(HttpSession session,@RequestParam(value="upload_fileName",required=false)MultipartFile mFile,HttpServletRequest request){
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"您还未登录，请先登录");
		 }
		 if(userServer.check_admin_role(user).isSuccess()){
			//同过session获得上下文及真实路径(即通过相对路径获得全路径)
			 String path = request.getSession().getServletContext().getRealPath("upload");
			 System.out.println("in controller path is " + path);
			 //上传文件
			 String targetFileName = iFileService.upload(mFile, path);
			 //拿到文件名的前缀路径
			 String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			 if(url != null || StringUtils.isNoneBlank(url)){
				 Map fileMap = Maps.newHashMap();
				 fileMap.put("uri",targetFileName);
				 fileMap.put("url",url);
				 return ServerResponse.createBySuccess(fileMap);
			 }
			 return ServerResponse.createByErrorMessage("url is null or blank");
		 }else{
			 return ServerResponse.createByErrorMessage("请以管理员身份登录");
		 }
		 
	 }
	 /*
	  * 
	  * 富文本文件上传
	  * 
	  * */
	 @RequestMapping(value="/richText_img_Upload",method=RequestMethod.POST)
	 @ResponseBody
	 public Map richTextUpload(HttpSession session,@RequestParam(value="upload_fileName",required=false)MultipartFile mFile,
			 HttpServletRequest request,HttpServletResponse response){
		 Map resultMap = Maps.newHashMap();
		 User user = (User)session.getAttribute(Const.CURRENT_USER);
		 if(user == null){
			 resultMap.put("success",false);
			 resultMap.put("msg","请登录管理员");
		 }
		 /*//富文本中对于返回值有自己的要求，我们使用是simditor 所以按照simditor的要求
		 {
			 "success":true/false,
			 "msg":"error message",#optional
			 "file_path":"[real file path]"
		 }*/
		 if(userServer.check_admin_role(user).isSuccess()){
			//同过session获得上下文及真实路径(即通过相对路径获得全路径)
			 String path = request.getSession().getServletContext().getRealPath("upload");
			 //上传文件
			 String targetFileName = iFileService.upload(mFile, path);
			 //拿到文件名的前缀路径
			 String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			 if(url != null || StringUtils.isNoneBlank(url)){
				 resultMap.put("success",true);
				 resultMap.put("msg","上传成功");
				 resultMap.put("file_path",url);
			 }else{
				 resultMap.put("success",false);
				 resultMap.put("msg","获取url失败");
			 }
			 
		 }else{
			 resultMap.put("success",false);
			 resultMap.put("msg","无权限操作");
			 
		 }
		 return resultMap;
		 
	 }


}
