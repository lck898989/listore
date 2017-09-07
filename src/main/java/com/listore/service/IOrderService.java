package com.listore.service;

import com.listore.commen.ServerResponse;

import java.util.Map;

/**
 * Created by HP on 2017/9/2.
 */
/*
*
* 订单接口
*
* */
public interface IOrderService {
    public ServerResponse pay(Integer userId,Long orderNo,String path);
    public ServerResponse aliCallback(Map<String,String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer id, Long orderNo);

    ServerResponse createOrder(Integer id, Integer shippingId);

    ServerResponse<String> cancelOrder(Integer id, Long orderNo);
}
