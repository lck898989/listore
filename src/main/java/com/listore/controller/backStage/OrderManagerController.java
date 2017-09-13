package com.listore.controller.backStage;

/**
 * Created by HP on 2017/9/7.
 */
/*
*
*
* 后台管理员进行订单的管理
*
*
* */

import com.github.pagehelper.PageInfo;
import com.listore.commen.Const;
import com.listore.commen.ResponseCode;
import com.listore.commen.ServerResponse;
import com.listore.pojo.User;
import com.listore.service.IOrderService;
import com.listore.service.IUserService;
import com.listore.vo.OrderItemVo;
import com.listore.vo.OrderVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {

    @Resource(name="iOrderService")
    private IOrderService iOrderService;
    @Resource(name="iUserService")
    private IUserService iUserService;

    //订单列表
    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("权限不够");
        }
        return iOrderService.manageList(pageNum,pageSize);
    }
    //订单详情
    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("权限不够");
        }
        if(orderNo == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
        }
        return iOrderService.manageDetail(orderNo);
    }
    //订单搜索
    @RequestMapping("/search")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                                @RequestParam(value="pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("权限不够");
        }
        if(orderNo == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
        }
        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }
    //订单发货
    @RequestMapping("/send_goods")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("权限不够");
        }
        if(orderNo == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGALE_ARGUMENT.getCode(),ResponseCode.ILLEGALE_ARGUMENT.getDesc());
        }
        return iOrderService.manageSendGoods(orderNo);
    }
}
