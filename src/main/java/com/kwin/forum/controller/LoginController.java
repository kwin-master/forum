package com.kwin.forum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController extends BaseController {
    /**
     * 访问登录页面
     * @return
     */
    @GetMapping(path = "/login")
    public String getLoginPage() {
        logger.info("转跳到登录页面");
        return "/site/login";
    }

    /**
     * 访问注册页面
     * @return
     */
    @GetMapping(path = "/register")
    public String getRegisterPage() {
        logger.info("转跳到注册界面");
        return "/site/register";
    }
}
