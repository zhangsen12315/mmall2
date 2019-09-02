package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest,Integer count, Integer productId){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,Integer count, Integer productId){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpServletRequest httpServletRequest,Integer productId){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }


    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user ==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
