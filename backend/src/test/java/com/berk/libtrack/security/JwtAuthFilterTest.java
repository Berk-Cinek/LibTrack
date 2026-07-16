package com.berk.libtrack.security;

import com.berk.libtrack.security.services.JwtService;
import com.berk.libtrack.security.services.impl.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private UserDetails userDetails;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_SetsMissingTokenError_WhenNoCookiesExist() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("auth_error", "MISSING_TOKEN");
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_SetsMissingTokenError_WhenJwtCookieIsMissing() throws ServletException, IOException {
        Cookie randomCookie = new Cookie("some_other_cookie", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{randomCookie});

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("auth_error", "MISSING_TOKEN");
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_SetsInvalidTokenError_WhenTokenIsInvalid() throws ServletException, IOException {
        Cookie jwtCookie = new Cookie("jwt", "invalid_token_string");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.isTokenValid("invalid_token_string")).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("auth_error", "INVALID_TOKEN");
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_SetsExpiredTokenError_WhenTokenIsExpired() throws ServletException, IOException {
        Cookie jwtCookie = new Cookie("jwt", "expired_token_string");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.isTokenValid("expired_token_string")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("auth_error", "EXPIRED_TOKEN");
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_AuthenticatesUser_WhenTokenIsValid() throws ServletException, IOException {
        String validToken = "valid_token_string";
        String username = "testuser";
        Cookie jwtCookie = new Cookie("jwt", validToken);

        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtService.isTokenValid(validToken)).thenReturn(true);
        when(jwtService.extractUsername(validToken)).thenReturn(username);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userDetails, auth.getPrincipal());

        verify(request, never()).setAttribute(eq("auth_error"), anyString());
        verify(filterChain).doFilter(request, response);
    }
}