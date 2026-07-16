package com.berk.libtrack.security.services.impl;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private final String secret = "this-is-a-very-secure-secret-key-that-needs-to-be-long-enough";
    private final long expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(secret, expirationMs);
    }

    @Test
    void generateToken_ReturnsValidToken() {
        String token = jwtService.generateToken("testuser");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUsername_ReturnsCorrectUsername() {
        String token = jwtService.generateToken("testuser");

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_ReturnsTrue() {
        String token = jwtService.generateToken("testuser");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_ReturnsFalseForInvalidToken() {
        String validToken = jwtService.generateToken("testuser");
        String invalidToken = validToken + "tampered";

        assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    }

    @Test
    void isTokenValid_ThrowsExpiredJwtException() throws InterruptedException {
        JwtServiceImpl shortLivedService = new JwtServiceImpl(secret, 1L);
        String token = shortLivedService.generateToken("testuser");

        Thread.sleep(10);

        assertThatThrownBy(() -> shortLivedService.isTokenValid(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}