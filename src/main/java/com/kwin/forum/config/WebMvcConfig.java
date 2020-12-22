package com.kwin.forum.config;

import com.kwin.forum.controller.interceptor.AlphaInterceptor;
import com.kwin.forum.controller.interceptor.LoginRequiredInterceptor;
import com.kwin.forum.controller.interceptor.LoginTicketInterceptor;
import com.kwin.forum.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 配置拦截器，指定拦截、排除的路径
         */
//        registry.addInterceptor(alphaInterceptor)
//                .excludePathPatterns("/**/*.css","/**/*.png ","/**/*.jpg","/**/*.jpeg")
//                .addPathPatterns("/register","/login");

        /**
         * 配置拦截器，不配置addPathPatterns时默认拦截所有path
         */
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png ","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png ","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png ","/**/*.jpg","/**/*.jpeg");
    }
}
