package com.listore.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.User;
import com.listore.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by HP on 2017/9/2.
 */
/*
* 订单模块开发
*
* */
@Controller
@RequestMapping("/order")
public class OrderController {

     private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
     @Resource(name="iOrderService")
     private IOrderService iOrderService;
     //创建订单
     @RequestMapping("/create")
     @ResponseBody
     public ServerResponse createOrder(HttpSession session,Integer shippingId){
         User user = (User)session.getAttribute(Const.CURRENT_USER);
         if(user == null){
             return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
         }
         return iOrderService.createOrder(user.getId(),shippingId);
     }
    //取消订单
    @RequestMapping("/cancel")
    @ResponseBody
    public ServerResponse<String> cancleOrder(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }
    //获取订单中购物车中的商品
    @RequestMapping("/get_order_cart_product")
    @ResponseBody
    public ServerResponse<String> getOrderCartProduct(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }












    //订单支付功能:需要订单号
    @RequestMapping("/pay")
    @ResponseBody
    public ServerResponse pay(HttpSession session, HttpServletRequest request,Long orderNo){
        //该路径没有###/upload/中的"/"
        String path = request.getSession().getServletContext().getRealPath("upload");
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.pay(user.getId(),orderNo,path);

    }
    //支付宝回调函数，需要给支付宝返回一个"success"字符串，如果支付宝没有收到该字符串的话，支付宝会一直发送通知
    @RequestMapping("/alipay_callback")
    @ResponseBody
    public Object callBack(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        //获得异步通知参数列表
        Map requestParams = request.getParameterMap();
        //用迭代器列出键值放到set中去，然后封装自己Map的key和value
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[])requestParams.get(name);
            String valueStr = "";
            for(int i = 0;i<values.length;i++){
                valueStr = (i == values.length - 1)?valueStr+values[0]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调:sign{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //重要信息：验证回调是不是支付宝发的，及防止重复发送通知
        //通知返回参数列表就是上面的params变量，里面有键值对就是一些参数信息
        //移除参数列表中sign_type参数，因为它不用验签
        params.remove("sign_type");

        logger.info(params.toString());
        try {
            logger.info("sign type is ",params.get("sign_type"));
            logger.info("sign is ",params.get("sign"));
            logger.info("publicKey is ",Configs.getAlipayPublicKey());
            logger.info("signType is ",Configs.getSignType());
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求");
            }
            logger.info(params.toString());
        } catch (AlipayApiException e) {
            logger.error("验证失败",e);
            e.printStackTrace();
        }
        //业务处理

        //验证通知的正确性:1:验证通知数据中订单号是否为商户系统中创建的订单号
        params.get("out_trade_no");
        //2:验证total_amount是否确实为该订单的实际金额

        //3:验证seller_id或者seller_email是否为out_trade_no这笔订单中的对应的操作方

        //验证成功的话那还费什么话快去数据库中更新信息啊
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            logger.info("1");
            return Const.AlipayCallback.RESPONSE_SUCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }
    //查询订单支付状态
    @RequestMapping("/query_order_pay_status")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createByError();
    }

}
