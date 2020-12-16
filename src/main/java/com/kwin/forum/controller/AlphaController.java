package com.kwin.forum.controller;

import com.kwin.forum.util.JsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/alpha")
public class AlphaController extends BaseController {
    //ajax示例
    @PostMapping(path = "/ajax")
    @ResponseBody
    public String testAjax(String name,int age) {
        System.out.println(name);
        System.out.println(age);
        return JsonUtils.getJSONString(0,"操作成功");
    }
}
