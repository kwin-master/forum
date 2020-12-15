package com.kwin.forum.controller;

import com.google.code.kaptcha.Producer;
import com.kwin.forum.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static com.kwin.forum.contants.LoginTicketContent.REMEMBER_EXPIRED_SECONDS;
import static com.kwin.forum.contants.LoginTicketContent.DEFAULT_EXPIRED_SECONDS;

@Controller
public class LoginController extends BaseController {
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ImageIO.write(image,"png",response.getOutputStream());
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }

    @PostMapping(path = "/login")
    public String login(String username, String password, String code, boolean remember,
                        Model model, HttpSession session, HttpServletResponse response) {
        logger.info(username + "请求登录");

        //检查验证码
        logger.info("检查验证码");
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确!");
            return "/site/login";
        }

        //检查账号密码
        logger.info("检查账号密码");
        int expiredSeconds = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if (map.containsKey("ticket")) {
            logger.info(username + "登录成功");
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            logger.error(username + "登录失败");
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping(path = "/logout")
    public String  logout(@CookieValue("ticket") String ticket) {
        logger.info("退出登录");
        userService.logout(ticket);
        return "redirect:/login";
    }
}
