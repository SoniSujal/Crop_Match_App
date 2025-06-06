package com.cropMatch.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final byte[] SECRET_KEY_BYTES = "e5f8d2a1b7c3d9e4f6a2b8c1d5e9f3a7b2c8d1e6f4a9b3c7d2e5f8a1b4c9d3e6".getBytes(StandardCharsets.UTF_8);
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    public String generateToken(String userEmail, String role) {
        return Jwts.builder()
                .setSubject(userEmail)
                .claim("role", role.toUpperCase()) // Ensure prefix and uppercase
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY_BYTES)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY_BYTES).parseClaimsJws(token).getBody();
    }

    public String extractUserEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        return role.startsWith("ROLE_") ? role : "ROLE_" + role; // Ensure prefix
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY_BYTES)  // Explicit encoding
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Token validation error: {}" +  e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}

