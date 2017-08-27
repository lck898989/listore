package com.listore.service.impl;

import javax.annotation.Resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.listore.commen.ResponseCode;
import com.listore.dao.ProductMapper;
import com.listore.pojo.Product;
import com.listore.util.BigDecimalUtil;
import com.listore.util.PropertiesUtil;
import com.listore.vo.CartProductVo;
import com.listore.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.listore.commen.Const;
import com.listore.commen.ServerResponse;
import com.listore.dao.CartMapper;
import com.listore.pojo.Cart;
import com.listore.service.ICartService;

import java.math.BigDecimal;
import java.util.List;

@Service("cartServer")
public class ICartServiceImpl implements ICartService {
	@Resource
	private CartMapper cartMapper;
	@Resource
	private ProductMapper productMapper;
	/*

	 * 添加商品到购物车中或者更新商品数
	 * 
	 * */
   //添加购物车
	@Override
	public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
		if(productId == null || count == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());

		}
		//先判断购物车里面有没有该对象存在
		Cart cart = cartMapper.selectByProductIdUserId(userId,productId);
		if(cart == null){
			Cart cartItem = new Cart();
			cart.setProductId(productId);
			cart.setUserId(userId);
			cart.setChecked(Const.Cart.CHECKED);
			cart.setQuantity(count);
			//说明购物车里面没有该商品,可以添加
			cartMapper.insert(cartItem);
		}else{
			//说明购物车里面有了该商品商品数量加一
			count = cart.getQuantity() + count;
			cart.setQuantity(count);
			//将更新持久化到数据库中去
			cartMapper.updateByPrimaryKeySelective(cart);

		}
		//封装一个VO对象返回给前端
		return this.listProduct(userId);
	}
    //更新购物车
	@Override
	public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
		if(productId == null || count == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		}
		//将该购物车取出来
		Cart cart = cartMapper.selectByProductIdUserId(userId, productId);
		if(cart != null){
			cart.setQuantity(count);
		}
		//将更新持久化到数据库中去
		cartMapper.updateByPrimaryKeySelective(cart);
		//先更新再展示给前端用户
		return this.listProduct(userId);
	}
    //删除购物车的产品(有可能多个产品需要删除所以传进来一个字符串然后进行分割)
	@Override
	public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
		if(productIds == null ){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		}
		//利用guava中的字符串分割方法
		List<String> productIdList = Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productIdList)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		}

		int deleteCount = cartMapper.deleteByUserIdProductIds(userId,productIdList);
		CartVo cartVo = new CartVo();
		cartVo = this.getCartVo(userId);
		if(deleteCount > 0){
			//说明删除成功
			return ServerResponse.createBySuccess("商品删除成功",cartVo);
		}else{
			return ServerResponse.createByErrorMessage("商品删除失败");
		}
	}

	@Override
	public ServerResponse<CartVo> listProduct(Integer userId) {
		CartVo cartVo = new CartVo();
		cartVo = this.getCartVo(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	//全选或者全反选:意味着一个用户的购物车的商品的状态都为1或者为0，只要确保传过来的参数userId和checked的值
	public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked){
          if(checked == null){
			  return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
		  }
		 int updateCount = cartMapper.selectOrUnSelectAll(userId,checked);
		//更新过后进行前端展示
		return this.listProduct(userId);

	}

	//将pojo对象封装成VO对象
	private CartVo getCartVo(Integer userId){
		//一个用户有多个购物车对象
		CartVo cartVo = new CartVo();
		List<Cart> cartList = cartMapper.selectByUserId(userId);
		List<CartProductVo> cartProductVoList = Lists.newArrayList();

		//声明购物车的总价
		BigDecimal cartTotalPrice = new BigDecimal("0");
        if(cartList != null){
			//循环遍历购物车列表
			for(Cart cartItem:cartList){
				//创建购物车产品对象
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(cartItem.getUserId());
				Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
				if(product != null){
					cartProductVo.setProductId(product.getId());
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProuductName(product.getName());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStock(product.getStock());
					int buyLimitCount = 0;
					//判断库存
					if(product.getStock() >= cartItem.getQuantity()){
						//库存充足的时候
						buyLimitCount = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_SUCCESS);
					}else{
						//库存不充足的时候购买的商品数量限制是：这里的cartItem.getQuentity是用户
						//自己填的购买数量，如果超过库存那么就在购物车对象中将其更新到最大库存
						buyLimitCount = product.getStock();
						//超出库存
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_FAIL);
						Cart cart = new Cart();
						cart.setId(cartItem.getId());
						cart.setQuantity(buyLimitCount);
						//更新购物车的商品数量
						cartMapper.updateByPrimaryKeySelective(cart);
					}
					cartProductVo.setQuantity(buyLimitCount);
					//计算购物车某一种商品的总价
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
					//设置该购物车产品是否被选中
					cartProductVo.setProductChecked(cartItem.getChecked());
				}
				//循环遍历每个购物车产品的时候看看当前遍历对象的产品是否被选中，如果被选中的话添加到购物车总价中去
				if(cartItem.getChecked() == Const.Cart.CHECKED){
				    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
				}
				//将当前的VO对象添加到集合中去
				cartProductVoList.add(cartProductVo);
			}

		}
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setCartTotalPrice(cartTotalPrice);
		//设置全选
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVo;
	}
	private boolean getAllCheckedStatus(Integer userId){
		if(userId == null){
			return false;
		}
		//如果用户的全选状态个数为零的话说明未勾选的产品为零个说明已经全选
		return cartMapper.selectCartProductCheckedByUserId(userId) == 0;
	}
	
}
