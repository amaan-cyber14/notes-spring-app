package com.example.notes_spring.component;

import com.example.notes_spring.service.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails user = (AppUserDetails) auth.getPrincipal();
        return user.getId();
    }
}