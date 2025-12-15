package com.example.notes_spring.repository;

import com.example.notes_spring.model.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    Optional<RefreshTokens> findByToken(String token);
}
