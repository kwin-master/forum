package com.kwin.forum.controller.advice;

import com.kwin.forum.controller.BaseController;
import com.kwin.forum.util.JsonUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ControllerAdvice 用来全局处理异常，annotation属性指定的@xx注解被使用的地方
 * 都被这个类来进行异常处理
 *
 * @ExpectionHandler 用于修饰方法，该方法会在Controller出现异常后被调用，用于处理捕获到的异常
 * @ModelAttribute 用于修饰方法，该方法会在Controller方法执行前被调用，用于为Model对象绑定参数
 * @DataBinder 用于修饰方法，该方法会在Controller方法执行前被调用，用于绑定参数的转换器
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice extends BaseController {
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常:" + e.getMessage());
        for (StackTraceElement element:e.getStackTrace()) {
            logger.error(element.toString());
        }

        /**
         * 先查看当前请求是不是异步ajax请求
         * ajax请求：直接输出一个json
         * 不是ajax请求：重定向到/error
         */
        String XRequestedWith = request.getHeader("x-requested-with");//x-requested-with=XMLHttpRequest用来表明是异步ajax请求
        if ("XMLHttpRequest".equals(XRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");//设置:文本类型-普通字符串;字符集-utf8
            PrintWriter writer = response.getWriter();
            writer.write(JsonUtils.getJSONString(1,"服务器异常!"));//先把普通字符串转成json,往网页中写一个json
        } else {
            response.sendRedirect(request.getContextPath() + "/error");//不是异步ajax请求，直接重定向到/error
        }
    }
}
