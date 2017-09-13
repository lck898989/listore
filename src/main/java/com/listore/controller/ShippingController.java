package com.listore.controller;

import com.github.pagehelper.PageInfo;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.Shipping;
import com.listore.pojo.User;
import com.listore.service.IShippingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * Created by HP on 2017/8/29.
 */
/*
*
*
* 收获地址控制层
*
* */
@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Resource(name="shippingService")
    private IShippingService iShippingService;
    @RequestMapping("/add")
    @ResponseBody
    //添加收获地址
    public ServerResponse add(HttpSession session,Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return iShippingService.add(user.getId(),shipping);

    }
    @RequestMapping("/delete")
    @ResponseBody
    //删除收获地址
    public ServerResponse<String> delete(HttpSession session,Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return iShippingService.delete(user.getId(),shippingId);

    }
    @RequestMapping("/update")
    @ResponseBody
    //更新收获地址
    public ServerResponse update(HttpSession session,Shipping shipping){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return iShippingService.update(user.getId(),shipping);

    }
    @RequestMapping("/select")
    @ResponseBody
    //更新收获地址
    public ServerResponse<Shipping> select(HttpSession session,Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return iShippingService.select(user.getId(),shippingId);

    }
    @RequestMapping("/list")
    @ResponseBody
    //更新收获地址
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10" )int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return iShippingService.selectByUserId(user.getId(),pageNum,pageSize);

    }

}
