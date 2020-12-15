package com.kwin.forum.controller;

import com.kwin.forum.entity.User;
import com.kwin.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

import static com.kwin.forum.contants.ForumContent.*;

@Controller
public class RegisterController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 注册账号
     * @param model
     * @param user
     * @return
     */
    @PostMapping(path = "/register")
    public String register(Model model, User user) {
        logger.info("尝试注册账号");
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            logger.info("注册成功，待激活");
            model.addAttribute("msg","注册成功,我们已经向你的邮件发送了一封激活邮件，请尽快激活!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            logger.info("注册失败");
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code) {
        logger.info("尝试激活账号");
        int result = userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS) {
            logger.info("激活成功，即将跳转登录页面");
            model.addAttribute("msg","激活成功,您的账号已经可以正常使用了!");
            //激活成功,跳回登录页面
            model.addAttribute("target","/login");
        } else if (result == ACTIVATION_REPEAT) {
            logger.info("激活无效，该账号重复激活");
            model.addAttribute("msg","无效操作,该账号已经激活过了!");
            //已激活,跳回首页
            model.addAttribute("target","/index");
        } else {
            logger.info("激活失败，激活码错误");
            model.addAttribute("msg","激活失败,您提供的激活码不正确!");
            //激活码错误,跳回首页
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
