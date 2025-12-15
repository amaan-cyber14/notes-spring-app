package com.example.notes_spring.repository;

import com.example.notes_spring.model.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    RefreshTokens findByUserId(Long userId);
}
