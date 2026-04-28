package com.washwise.feature.auth;

import com.washwise.shared.security.JwtService;

import com.washwise.feature.auth.dto.LoginRequest;
import com.washwise.feature.auth.dto.RegisterRequest;
import com.washwise.feature.auth.dto.AuthResponse;
import com.washwise.feature.auth.dto.TokenResponse;
import com.washwise.feature.auth.entity.RefreshToken;
import com.washwise.feature.user.entity.User;
import com.washwise.feature.user.entity.UserRole;
import com.washwise.shared.exception.DuplicateResourceException;
import com.washwise.shared.exception.InvalidCredentialsException;
import com.washwise.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * Handles user registration and login
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Register a new user
     * Validates input, checks for duplicates, hashes password, saves user
     * 
     * @param request registration request containing email, password, fullName
     * @return AuthResponse with user data and tokens
     * @throws DuplicateResourceException if email already exists
     * @throws IllegalArgumentException if passwords don't match or validation fails
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email already exists - {}", request.getEmail());
            throw new DuplicateResourceException("Email already registered");
        }

        // Create new user entity
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName() != null ? request.getFullName() : "User")
                .role(UserRole.CUSTOMER)  // New users are customers by default
                .build();

        // Save user to database
        user = userRepository.save(user);
        log.info("User registered successfully: {} with ID: {}", user.getEmail(), user.getId());

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Return response
        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    /**
     * Login user
     * Validates credentials and generates tokens
     * 
     * @param request login request containing email and password
     * @return AuthResponse with user data and tokens
     * @throws InvalidCredentialsException if email not found or password wrong
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Use a single error message regardless of whether the email exists or
        // the password is wrong — leaking which one fails would let an attacker
        // enumerate registered accounts.
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = jwtService.generateToken(user.getEmail());

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    /**
     * Build AuthResponse from user and tokens
     * Helper method to construct response DTO
     * 
     * @param user the authenticated user
     * @param accessToken JWT access token
     * @param refreshTokenValue refresh token value
     * @return populated AuthResponse
     */
    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshTokenValue) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    /**
     * Validate JWT token (basic validation)
     * Used by security filter to validate tokens
     * 
     * @param token JWT token to validate
     * @return true if token is valid
     */
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    /**
     * Extract username from token
     * Used to identify user from token
     * 
     * @param token JWT token
     * @return username/email from token
     */
    public String getUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    /**
     * Refresh access token using refresh token
     * 
     * @param refreshToken refresh token string
     * @return TokenResponse with new access token
     */
    public TokenResponse refreshAccessToken(String refreshToken) {
        log.info("Refreshing access token");

        RefreshToken validToken = refreshTokenService.validateRefreshToken(refreshToken);
        String newAccessToken = jwtService.generateToken(validToken.getUser().getEmail());

        return TokenResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtService.getExpirationTime())
            .build();
    }
}