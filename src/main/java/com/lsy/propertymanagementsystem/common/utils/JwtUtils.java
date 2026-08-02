package com.lsy.propertymanagementsystem.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class JwtUtils {
    private static String secret;
    private static long expiration = 24 * 60 * 60 * 1000;

    private static final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public static void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    public static void setExpiration(long expiration) {
        JwtUtils.expiration = expiration;
    }

    private static SecretKey getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT secret not configured");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(Long userId, String username, String roleKey, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleKey", roleKey);
        claims.put("permissions", permissions);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Claims parseClaims(String token) {
        try {
            return parseToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public static Long getUserIdFromClaims(Claims claims) {
        Object value = claims.get("userId");
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Long) return (Long) value;
        return null;
    }

    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public static String getUsernameFromClaims(Claims claims) {
        return claims.get("username", String.class);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getPermissions(String token) {
        Claims claims = parseToken(token);
        List<String> perms = claims.get("permissions", List.class);
        return perms != null ? perms : Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getPermissionsFromClaims(Claims claims) {
        List<String> perms = claims.get("permissions", List.class);
        return perms != null ? perms : Collections.emptyList();
    }

    public static String getRoleKey(String token) {
        Claims claims = parseToken(token);
        return claims.get("roleKey", String.class);
    }

    public static String getRoleKeyFromClaims(Claims claims) {
        return claims.get("roleKey", String.class);
    }

    public static boolean isTokenValid(String token) {
        try {
            if (tokenBlacklist.containsKey(token)) {
                return false;
            }
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public static void invalidateToken(String token) {
        if (token != null) {
            tokenBlacklist.put(token, System.currentTimeMillis());
        }
    }

    public static void cleanExpiredBlacklist() {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> (now - entry.getValue()) > expiration);
    }
}
