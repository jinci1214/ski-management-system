package com.ski.ticketing.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private JwtUtil() {
    }

    public static String createJWT(String secretKey, long ttl, Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(key)
                .compact();
    }

    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static Long getEmpIdFromToken(String secretKey, String token) {
        Claims claims = parseJWT(secretKey, token);
        return claims.get("empId", Long.class);
    }

    public static Long getUserIdFromToken(String secretKey, String token) {
        Claims claims = parseJWT(secretKey, token);
        return claims.get("userId", Long.class);
    }

    public static boolean validateToken(String secretKey, String token) {
        try {
            parseJWT(secretKey, token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}