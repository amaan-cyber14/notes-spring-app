package com.example.notes_spring.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMs;


    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {

        // if secret is base64, you may decode it; here we'll use raw bytes
        byte [] decodeBase64 = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(decodeBase64);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            final String sub = extractSubject(token);
            return (sub != null && sub.equals(username) && !isTokenExpired(token));
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.before(new Date());
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



}
