package com.lsy.propertymanagementsystem.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    @Test
    void logoutOnlyInvalidatesCurrentTab() {
        JwtUtils.setSecret("testSecretKeyForJwtTokenGenerationTestOnly123456789");
        String token = JwtUtils.generateToken(1L, "tester");

        assertTrue(JwtUtils.isTokenValid(token, "tab-a"));
        assertTrue(JwtUtils.isTokenValid(token, "tab-b"));

        JwtUtils.invalidateToken(token, "tab-a");

        assertFalse(JwtUtils.isTokenValid(token, "tab-a"));
        assertTrue(JwtUtils.isTokenValid(token, "tab-b"));
    }
}
