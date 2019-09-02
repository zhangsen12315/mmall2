package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.OrderVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpSession session,HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){

//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpSession session, HttpServletRequest httpServletRequest,Long orderNo){

//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpSession session, HttpServletRequest httpServletRequest, Long orderNo, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpSession session,HttpServletRequest httpServletRequest, Long orderNo){

//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(iUserService.checkRoleIsAdmin(user).isSuccess()){
            //填充我们增加产品的业务逻辑
            return iOrderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


}
