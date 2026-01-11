package com.example.notes_spring.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.example.notes_spring.interceptor.RateLimitInterceptor;

@Configuration
public class WebConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;


    public WebConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    
        registry.addInterceptor(rateLimitInterceptor)
        .addPathPatterns("/api/**");
    }
    
}
