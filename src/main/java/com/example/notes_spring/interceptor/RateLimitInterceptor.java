package com.example.notes_spring.interceptor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.notes_spring.component.AuthUtil;
import com.example.notes_spring.service.RateLimitService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final AuthUtil authUtil;

    public RateLimitInterceptor(RateLimitService rateLimitService, AuthUtil authUtil) {
        this.rateLimitService = rateLimitService;
        this.authUtil = authUtil;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
          String key;
          
            if(SecurityContextHolder.getContext().getAuthentication() != null) {
                key = "USER_" + authUtil.getCurrentUserId();
          }
          else {
                key = request.getRemoteAddr();
          }

        if (!rateLimitService.isRequestAllowed(key)) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Too many requests");
            return false;
        }

        return true;
    }
    
}
