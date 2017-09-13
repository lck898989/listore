package com.listore.service;

import com.github.pagehelper.PageInfo;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Shipping;

/**
 * Created by HP on 2017/8/29.
 */
public interface IShippingService {

    ServerResponse add(Integer userId,Shipping shipping);

    ServerResponse<String> delete(Integer id, Integer shippingId);

    ServerResponse update(Integer id, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> selectByUserId(Integer userId,int pageNum,int pageSize);
}
