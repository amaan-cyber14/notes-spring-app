package com.example.notes_spring.controller;

import com.example.notes_spring.model.User;
import com.example.notes_spring.repository.UserRepository;
import com.example.notes_spring.service.JwtService;
import com.example.notes_spring.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public static class SignupRequest {
        public String email;
        public String password;
    }

    public static class AuthResponse {
        public String token;
        public Long userId;

        public AuthResponse(String token, Long userId) { this.token = token; this.userId = userId; }
    }

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody @Validated SignupRequest req) {
        User user = userService.register(req.email, req.password);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getId());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Validated SignupRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password)
        );
        String token = jwtService.generateToken(req.email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(req.email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AuthResponse(token, user.getId());
    }
}
