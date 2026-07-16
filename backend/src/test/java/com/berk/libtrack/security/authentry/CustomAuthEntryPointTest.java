package com.berk.libtrack.security.authentry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private CustomAuthEntryPoint customAuthEntryPoint;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void commence_handlesMissingToken() throws IOException {
        when(request.getAttribute("auth_error")).thenReturn("MISSING_TOKEN");

        customAuthEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String expectedJson = "{\"status\":401,\"error\":\"MISSING_TOKEN\",\"message\":\"You must be logged in to access this resource.\"}";
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    void commence_handlesInvalidOrExpiredToken() throws IOException {
        when(request.getAttribute("auth_error")).thenReturn("INVALID_OR_EXPIRED_TOKEN");

        customAuthEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String expectedJson = "{\"status\":401,\"error\":\"INVALID_OR_EXPIRED_TOKEN\",\"message\":\"Your session has expired. Please log in again.\"}";
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    void commence_handlesNullReason() throws IOException {
        when(request.getAttribute("auth_error")).thenReturn(null);

        customAuthEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String expectedJson = "{\"status\":401,\"error\":\"null\",\"message\":\"Authentication required.\"}";
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }

    @Test
    void commence_handlesUnknownReason() throws IOException {
        when(request.getAttribute("auth_error")).thenReturn("SOME_OTHER_ERROR");

        customAuthEntryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");

        String expectedJson = "{\"status\":401,\"error\":\"SOME_OTHER_ERROR\",\"message\":\"Authentication required.\"}";
        assertThat(stringWriter.toString()).isEqualTo(expectedJson);
    }
}