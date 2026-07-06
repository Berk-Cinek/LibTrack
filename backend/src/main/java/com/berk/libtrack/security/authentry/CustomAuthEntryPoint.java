package com.berk.libtrack.security.authentry;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String reason = (String) request.getAttribute("auth_error");
        String message;

        if ("MISSING_TOKEN".equals(reason)) {
            message = "You must be logged in to access this resource.";
        } else if ("INVALID_OR_EXPIRED_TOKEN".equals(reason)) {
            message = "Your session has expired. Please log in again.";
        } else {
            message = "Authentication required.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":401,\"error\":\"" + reason + "\",\"message\":\"" + message + "\"}"
        );
    }
}
