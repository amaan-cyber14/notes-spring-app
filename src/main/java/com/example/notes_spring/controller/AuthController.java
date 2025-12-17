package com.example.notes_spring.controller;

import com.example.notes_spring.dto.RefreshTokenRequest;
import com.example.notes_spring.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public static class SignupRequest {
        public String email;
        public String password;
    }

    public static class AuthResponse {
        public String token;
        public Long userId;
        public String refreshToken;

        public AuthResponse(String token, Long userId, String refreshToken) { this.token = token; this.userId = userId; this.refreshToken = refreshToken; }
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody @Validated SignupRequest req) {
        return authService.signup(req);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Validated RefreshTokenRequest req) {
        return authService.refresh(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Validated SignupRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody @Validated RefreshTokenRequest req) {
        return authService.logout(req);
    }
}
