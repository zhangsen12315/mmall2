package com.mmall.controller.protal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class OrderController {

    private static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest,Long orderNo, HttpServletRequest request){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest,Integer shippingId){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest,Long orderNo){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iOrderService.cancel(user.getId(),orderNo);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest httpServletRequest,Long orderNo){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for (Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();){
            String name = (String)iterator.next();
            String[] values = (String[])requestParams.get(name);
            String valueStr = "";
            for (int i=0; i<values.length; i++){
               valueStr = (i==values.length-1)?valueStr+values[i]:valueStr+values[i]+".";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //非常重要验证回调的正确性,是不是支付宝发的,并且还要必要重复通知
        params.remove("sign_type");
        try {
           boolean alipayRSACheckdV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
           if (!alipayRSACheckdV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报网警了");
           }
        } catch (AlipayApiException e) {
           logger.error("支付宝验证回调异常",e);
        }

        //TODO 验证各种数据

        ServerResponse response = iOrderService.aliCallback(params);
        if (response.isSuccess()){
             return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest httpServletRequest,Long orderNo, HttpServletRequest request){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);

        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
         return ServerResponse.createBySuccess(false);
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }


}
