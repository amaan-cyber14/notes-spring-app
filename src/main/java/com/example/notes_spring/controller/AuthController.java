package com.example.notes_spring.controller;

import com.example.notes_spring.component.AuthUtil;
import com.example.notes_spring.dto.RefreshTokenRequest;
import com.example.notes_spring.model.RefreshTokens;
import com.example.notes_spring.model.User;
import com.example.notes_spring.repository.RefreshTokenRepository;
import com.example.notes_spring.service.JwtService;
import com.example.notes_spring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
/*
*
* Need to refactor, remove all the repos, and create and use AuthService
* */

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          RefreshTokenRepository refreshTokenRepository,
                          AuthUtil authUtil
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUtil = authUtil;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
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
        User user = userService.register(req.email, req.password);
        Long userId = user.getId();
        String token = jwtService.generateToken(user.getEmail());
        long createdAt = System.currentTimeMillis();
        long expiresAt = createdAt + 24L * 60 * 60 * 1000 * 30 ;
        RefreshTokens refreshTokens = new RefreshTokens(
            UUID.randomUUID().toString(), userId, createdAt, expiresAt, false
        );

        RefreshTokens savedRefreshToken = refreshTokenRepository.save(refreshTokens);

        return new AuthResponse(token, user.getId(), savedRefreshToken.getToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody @Validated RefreshTokenRequest req) {
        Long currentUserId = authUtil.getCurrentUserId();
        RefreshTokens dbRefreshToken = refreshTokenRepository.findByUserId(currentUserId);
        boolean isRefreshTokenValid = !dbRefreshToken.isRevoked() && (dbRefreshToken.getCreatedAt() < dbRefreshToken.getExpiresAt()) && Objects.equals(dbRefreshToken.getToken(), req.getRefreshToken());
        if (isRefreshTokenValid) {
            String currentUserEmail = authUtil.getCurrentUserEmail();
            String token = jwtService.generateToken(currentUserEmail);
            return new ResponseEntity<>(new AuthResponse(token, currentUserId, req.getRefreshToken()), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Validated SignupRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password)
        );
        String token = jwtService.generateToken(req.email);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        long createdAt = System.currentTimeMillis();
        Long userId = authUtil.getCurrentUserId();
        long expiresAt = createdAt + 24L * 60 * 60 * 1000 * 30 ;
        RefreshTokens refreshTokens = new RefreshTokens(
                UUID.randomUUID().toString(), userId, createdAt, expiresAt, false
        );
        RefreshTokens savedRefreshToken = refreshTokenRepository.save(refreshTokens);
        return new AuthResponse(token, userId, savedRefreshToken.getToken());
    }
}
