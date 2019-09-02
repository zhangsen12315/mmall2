package com.mmall.controller.protal;


import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
//            session.setAttribute(Const.CURRENT_USER,response.getData());
            //3CA4DC18176BF72CAF1BA00D9131F76B
            //3CA4DC18176BF72CAF1BA00D9131F76B

            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//            CookieUtil.readLoginCookie(httpServletRequest);
//            CookieUtil.delLoginToken(httpServletResponse,httpServletRequest);
            RedisPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method =RequestMethod.POST)
    public ServerResponse logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
//        session.removeAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(httpServletRequest);
        CookieUtil.delLoginToken(httpServletResponse,httpServletRequest);
        if (!StringUtils.isEmpty(loginToken)){
            RedisPoolUtil.del(loginToken);
        }
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse register(User user){
        ServerResponse serverResponse = iUserService.register(user);
        return serverResponse;
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserInfo(HttpSession session,HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if(user != null){
//            return ServerResponse.createBySuccess(user);
//        }
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        if(user != null){
           return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetgetQuestion(String username){
        ServerResponse serverResponse = iUserService.forGetgetQuestion(username);
        return serverResponse;
    }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse forgetCheckAnswer(String username,String question,String answer){
        ServerResponse serverResponse = iUserService.checkAnswer(username,question,answer);
        return serverResponse;
    }

    @RequestMapping(value = "forget_reset_password.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
        return  iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPssword(String passwordOld,String passwordNew,HttpServletRequest httpServletRequest,HttpSession session){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User user = JsonUtil.string2Obj(redisValue, User.class);
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value = "update_information.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest httpServletRequest,User user){
//        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User currentUser = JsonUtil.string2Obj(redisValue, User.class);
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> serverResponse = iUserService.updateInformation(user);
        if (serverResponse.isSuccess()){
//            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
            RedisPoolUtil.setEx(cookie, JsonUtil.obj2String(serverResponse.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return serverResponse;
    }

    @RequestMapping(value = "get_information.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfoById(HttpSession session,HttpServletRequest httpServletRequest){
//        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        String cookie = CookieUtil.readLoginCookie(httpServletRequest);
        if (StringUtils.isEmpty(cookie)){
            return ServerResponse.createByErrorMessage("cookie为空用户未登录,无法获取当前用户的信息");
        }
        String redisValue = RedisPoolUtil.get(cookie);
        User currentUser = JsonUtil.string2Obj(redisValue, User.class);
        ServerResponse serverResponse = iUserService.findUserInfoById(currentUser.getId());
        return serverResponse;
    }

}
