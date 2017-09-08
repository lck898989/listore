package com.listore.service;

import com.github.pagehelper.PageInfo;
import com.listore.commen.ServerResponse;
import com.listore.vo.OrderItemVo;
import com.listore.vo.OrderVo;

import java.util.List;
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
    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> detail(Integer id, Long orderNo);

    ServerResponse<PageInfo> list(Integer id,Integer pageNum,Integer pageSize);
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);

    public ServerResponse<String> manageSendGoods(long orderNo);
}
