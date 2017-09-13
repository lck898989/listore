package com.listore.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.dao.ShippingMapper;
import com.listore.pojo.Shipping;
import com.listore.service.IShippingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by HP on 2017/8/29.
 */
@Service("shippingService")
public class IShippingServiceImpl implements IShippingService{
    @Resource
    private ShippingMapper shippingMapper;
    /*
    * mybatis 获得主键的生效函数及拿到其自动生成的ID
    *
    * */
    @Override
    public ServerResponse add(Integer userId,Shipping shipping) {
        if(shipping != null){
            shipping.setUserId(userId);
            int insertCount = shippingMapper.insert(shipping);
            if(insertCount > 0){
                Map resultMap = Maps.newHashMap();
                resultMap.put("shippingId",shipping.getId());
                return ServerResponse.createBySuccess("添加地址成功",resultMap);
            }
            return ServerResponse.createByErrorMessage("添加地址失败");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
    }

    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId) {
        if(userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());

        }
        int deleteCount = shippingMapper.deleteByUserIdShippingId(userId,shippingId);
        if(deleteCount > 0){
            return ServerResponse.createBySuccessMsg("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId,Shipping shipping) {
        if(userId == null || shipping == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
        }
        //为什么更新还要重新set:存在安全漏洞，假如前端传过来了userId不是当前userId那么就会改动别人的收获地址
        shipping.setUserId(userId);
        int updateRow = shippingMapper.updateByShipping(shipping);
        if(updateRow > 0){
            return ServerResponse.createBySuccessMsg("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }
    //收获地址列表
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        if(userId == null || shippingId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if(shipping != null){
            return ServerResponse.createBySuccess("获取成功",shipping);
        }
        return ServerResponse.createByErrorMessage("获取失败");

    }
    //获得收货地址的列表
    @Override
    public ServerResponse<PageInfo> selectByUserId(Integer userId,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        //对下面的SQL语句进行拦截分页
        List<Shipping> shippingList = Lists.newArrayList();
        shippingList = shippingMapper.selectByUserId(userId);
        PageInfo result = new PageInfo(shippingList);
        if(result != null){
            return ServerResponse.createBySuccess(result);
        }
        return ServerResponse.createByErrorMessage("查询失败");
    }
}
