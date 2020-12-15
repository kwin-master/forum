package com.kwin.forum.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class LoginController extends BaseController {
    @Autowired
    private Producer kaptchaProducer;

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

    @GetMapping(path = "/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        logger.info("生成验证码");
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        logger.info("将验证码存入session");
        session.setAttribute("kaptcha",text);

        logger.info("将图片输出给浏览器");
        response.setContentType("image/png");
        try {
            ImageIO.write(image,"png",response.getOutputStream());
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }
}
