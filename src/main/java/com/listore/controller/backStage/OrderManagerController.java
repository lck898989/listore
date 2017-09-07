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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {
    @Resource(name="iOrderService")
    private IOrderService iOrderService;

    //订单列表
    public ServerResponse<PageInfo> orderList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("权限不够");
        }
        iOrderService.list(null,pageNum,pageSize);
    }


}
