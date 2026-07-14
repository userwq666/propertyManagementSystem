package com.lsy.propertymanagementsystem.config;

import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @PostConstruct
    public void init() {
        JwtUtils.setSecret(secret);
        JwtUtils.setExpiration(expiration);
    }
}