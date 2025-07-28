package com.example.demo.config;

import com.example.demo.filter.RequestCachingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {
    @ConditionalOnBean(SignatureConfig.class)
    @Bean
    public FilterRegistrationBean<RequestCachingFilter> requestCachingFilterRegistration(
            RequestCachingFilter requestCachingFilter) {
        FilterRegistrationBean<RequestCachingFilter> bean = new FilterRegistrationBean<>(requestCachingFilter);
        bean.setOrder(1);
        return bean;
    }
}
