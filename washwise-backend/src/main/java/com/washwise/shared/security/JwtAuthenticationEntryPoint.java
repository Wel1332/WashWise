package com.washwise.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point
 * Handles authentication errors (401 Unauthorized)
 * Returns JSON error response instead of default HTML page
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Commence authentication exception
     * Called when unauthenticated user tries to access protected resource
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authException the authentication exception
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.error("Unauthorized access attempt: {}", authException.getMessage());

        // Set response content type to JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Build error response body
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("message", "Unauthorized - Authentication required");
        body.put("error", authException.getMessage());
        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getServletPath());

        // Write error response as JSON
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}