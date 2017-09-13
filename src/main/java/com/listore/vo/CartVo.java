package com.listore.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HP on 2017/8/26.
 */
/*
*
* 购物车试图对象：即展示给前端客户看的一个抽象对象
*
* */
public class CartVo {
    //购物车里面的商品对象列表
    List<CartProductVo> cartProductVoList;
    //是否全选
    private boolean allChecked;
    //购物车中商品的总价
    private BigDecimal cartTotalPrice;
    //商品主图的地址
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
