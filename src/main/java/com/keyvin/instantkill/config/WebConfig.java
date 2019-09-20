package com.keyvin.instantkill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/14 0:34
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private UserArgumentResolver userArgumentResolver;

    //controller方法的参数的赋值，进controller之前做的
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }
}
