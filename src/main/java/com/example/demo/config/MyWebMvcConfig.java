package com.example.demo.config;

import com.example.demo.interceptor.IpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private IpInterceptor ipInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册IP拦截器
        registry.addInterceptor(ipInterceptor);
    }
}
