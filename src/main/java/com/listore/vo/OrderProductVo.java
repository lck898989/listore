package com.listore.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HP on 2017/9/7.
 */
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private String imageHost;
    private BigDecimal productTotalPrice;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
}
