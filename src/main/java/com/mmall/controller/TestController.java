package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/test/")
public class TestController {

    @RequestMapping("test.do")
    @ResponseBody
    public void test(){
        System.out.println("test");
    }

}
