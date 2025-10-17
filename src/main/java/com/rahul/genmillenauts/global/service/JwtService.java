package com.rahul.genmillenauts.global.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.global.config.CustomUserDetails;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 🔹 Generate token with userId included
    public String generateToken(CustomUserDetails userDetails) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getRoleName())
                .claim("userId", userDetails.getId()) // ✅ include userId
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // 🔹 Extract username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔹 Extract authorities (role)
    public List<GrantedAuthority> getAuthorities(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // 🔹 Validate token
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
