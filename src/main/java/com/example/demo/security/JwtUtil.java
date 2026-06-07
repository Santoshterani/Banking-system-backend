package com.example.demo.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(
                    "mysecurebankingprojectsecretkey12345"
                            .getBytes()
            );

    public static String generateToken(
            String email,
            String role
    ) {

    	return Jwts.builder()
    	        .subject(email)
    	        .claim("role", role)
    	        .issuedAt(new Date())
    	        .expiration(
    	                new Date(
    	                        System.currentTimeMillis() + 86400000
    	                )
    	        )
    	        .signWith(KEY)
    	        .compact();
    }

    public static Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String extractEmail(
            String token
    ) {
        return extractClaims(token)
                .getSubject();
    }

    public static String extractRole(
            String token
    ) {
        return extractClaims(token)
                .get("role", String.class);
    }
}