package com.example.notes_spring.service;

import com.example.notes_spring.model.User;
import com.example.notes_spring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("Email already registered");
        }
        // hashing password
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword);
        return userRepository.save(user);
    }
}
