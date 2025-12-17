package com.example.notes_spring.service;

import com.example.notes_spring.component.AuthUtil;
import com.example.notes_spring.controller.AuthController;
import com.example.notes_spring.dto.RefreshTokenRequest;
import com.example.notes_spring.exception.UnauthorizedException;
import com.example.notes_spring.model.RefreshTokens;
import com.example.notes_spring.model.User;
import com.example.notes_spring.repository.RefreshTokenRepository;
import com.example.notes_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;


    public AuthService(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          RefreshTokenRepository refreshTokenRepository,
                          UserRepository userRepository,
                          AuthUtil authUtil
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUtil = authUtil;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public ResponseEntity<Map<String, String>> logout(@RequestBody @Validated RefreshTokenRequest req) {
        String refreshToken = req.getRefreshToken();

        RefreshTokens refreshTokens = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        refreshTokenRepository.deleteByUserId(refreshTokens.getUserId());
        HashMap<String, String> map = new HashMap<>();
        map.put("refreshToken", null);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @Transactional
    public AuthController.AuthResponse login(AuthController.SignupRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(req.email);

        Long userId = authUtil.getCurrentUserId();

        log.debug("UserId = {}", userId.toString());

        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.flush();

        RefreshTokens refreshTokens = generateRefreshToken(userId);

        RefreshTokens savedRefreshToken = refreshTokenRepository.save(refreshTokens);
        return new AuthController.AuthResponse(token, userId, savedRefreshToken.getToken());
    }


    public AuthController.AuthResponse signup(AuthController.SignupRequest req) {
        User user = userService.register(req.email, req.password);
        String token = jwtService.generateToken(user.getEmail());
        Long userId = user.getId();
        RefreshTokens refreshTokens = generateRefreshToken(userId);

        RefreshTokens savedRefreshToken = refreshTokenRepository.save(refreshTokens);

        return new AuthController.AuthResponse(token, user.getId(), savedRefreshToken.getToken());
    }

    public AuthController.AuthResponse refresh(RefreshTokenRequest req) {
        RefreshTokens dbRefreshToken = refreshTokenRepository.findByToken(req.getRefreshToken()).orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        boolean isRefreshTokenValid = !dbRefreshToken.isRevoked() && (dbRefreshToken.getCreatedAt() < dbRefreshToken.getExpiresAt());
        if (isRefreshTokenValid) {
            String currentUserEmail = userRepository.findById(dbRefreshToken.getUserId()).orElseThrow().getEmail();
            String token = jwtService.generateToken(currentUserEmail);
            return new AuthController.AuthResponse(token, dbRefreshToken.getUserId(), req.getRefreshToken());
        } else {
            throw new UnauthorizedException("Refresh token revoked or expired");
        }
    }

    RefreshTokens generateRefreshToken(Long userId) {
        long createdAt = System.currentTimeMillis();
        long expiresAt = createdAt + 24L * 60 * 60 * 1000 * 30 ;
        return new RefreshTokens(
                UUID.randomUUID().toString(), userId, createdAt, expiresAt, false
        );
    }

}
