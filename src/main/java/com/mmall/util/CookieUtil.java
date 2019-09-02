package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {

    private static final String COOKIE_DOMAIN = ".happymmall.com";
    private static final String COOKIE_NAME = "mmall_login_token";

    public static String readLoginCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
           for (Cookie c : cookies){
               log.info("read cookieName:{},cookieValue:{}",c.getName(),c.getValue());
               if (StringUtils.equals(COOKIE_NAME,c.getName())){
                   log.info("return cookieName:{},cookieValue:{}",c.getName(),c.getValue());
                   return c.getValue();
               }
           }
        }
        return null;
    }


    /**
     *
     * @param response
     * @param token
     */

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");  //代表设置在根目录

        //单位是秒 如果这么maxage不设置的话,cookie就不会写入硬盘,而是只写在内存。只在当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 365);  //如果是-1,代表永久
        log.info("wrtie cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    public static void delLoginToken(HttpServletResponse response,HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie c : cookies){
                if (StringUtils.equals(c.getName(),COOKIE_NAME)){
                    c.setDomain(COOKIE_DOMAIN);
                    c.setPath("/");
                    c.setMaxAge(0);
                    log.info("del cookieName:{},cookieValue:{}",c.getName(),c.getValue());
                    response.addCookie(c);
                    return;
                }
            }
        }
    }
}
