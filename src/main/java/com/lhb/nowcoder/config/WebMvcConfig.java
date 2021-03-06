package com.lhb.nowcoder.config;

import com.lhb.nowcoder.controller.interceptor.DataInterceptor;
import com.lhb.nowcoder.controller.interceptor.LoginRequireInterceptor;
import com.lhb.nowcoder.controller.interceptor.LoginTicketInterceptor;
import com.lhb.nowcoder.controller.interceptor.MessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;

//    使用spring security提高系统的安全性
//    @Resource
//    private LoginRequireInterceptor loginRequireInterceptor;

    @Resource
    private MessageInterceptor messageInterceptor;

    @Resource
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

//        使用spring security提高系统的安全性
//        registry.addInterceptor(loginRequireInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
