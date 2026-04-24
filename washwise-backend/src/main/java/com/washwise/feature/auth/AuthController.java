package com.washwise.feature.auth;

import com.washwise.feature.auth.dto.LoginRequest;
import com.washwise.feature.auth.dto.RefreshTokenRequest;
import com.washwise.feature.auth.dto.RegisterRequest;
import com.washwise.shared.dto.ApiResponse;
import com.washwise.feature.auth.dto.AuthResponse;
import com.washwise.feature.auth.dto.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * REST endpoints for user registration and login
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * POST /api/v1/auth/register
     * 
     * @param request registration request with email, password, fullName
     * @return 201 Created with user data and tokens
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with email, password, and full name")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Register endpoint called for email: {}", request.getEmail());
        
        AuthResponse authResponse = authService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        authResponse,
                        "User registered successfully",
                        HttpStatus.CREATED.value()
                ));
    }

    /**
     * Login user
     * POST /api/v1/auth/login
     * 
     * @param request login request with email and password
     * @return 200 OK with user data and tokens
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT access token and refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        log.info("Login endpoint called for email: {}", request.getEmail());
        
        AuthResponse authResponse = authService.login(request);
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        authResponse,
                        "Login successful",
                        HttpStatus.OK.value()
                ));
    }

    /**
     * Health check endpoint (public)
     * GET /api/v1/auth/health
     * 
     * @return 200 OK
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if authentication service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity
                .ok(ApiResponse.success(
                        "OK",
                        "Auth service is healthy",
                        HttpStatus.OK.value()
                ));
    }

    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     * 
     * @param request refresh token request
     * @return 200 OK with new access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token endpoint called");
        TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(
                ApiResponse.<TokenResponse>builder()
                .success(true)
                .data(response)
                .message("Access token refreshed successfully")
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build());
    }
}