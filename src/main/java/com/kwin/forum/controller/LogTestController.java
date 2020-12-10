package com.kwin.forum.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogTestController {
    private static final Logger log = LoggerFactory.getLogger(LogTestController.class);

    @GetMapping(value = "test")
    @ResponseBody
    public String test() {
        log.info("这是log4j2的日志测试，info级别");
        log.warn("这是log4j2的日志测试，warn级别");
        log.error("这是log4j2的日志测试，error级别");

        return "HELLO_BUG";
    }
}
