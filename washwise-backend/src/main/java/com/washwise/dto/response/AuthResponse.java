package com.washwise.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.washwise.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for authentication response
 * Sent to client after successful login/register
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * User's unique identifier
     */
    private UUID id;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's full name
     */
    private String fullName;

    /**
     * User's role
     * Determines access levels and permissions
     */
    private UserRole role;

    /**
     * JWT access token
     * Used for authenticating subsequent requests
     * Short-lived (1 hour)
     */
    @JsonProperty("accessToken")
    private String accessToken;

    /**
     * JWT refresh token
     * Used to obtain new access token
     * Long-lived (7 days)
     */
    @JsonProperty("refreshToken")
    private String refreshToken;

    /**
     * Token type (always "Bearer")
     * Used in Authorization header: "Bearer {token}"
     */
    @JsonProperty("tokenType")
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access token expiration time in milliseconds
     * Helps client know when to refresh
     */
    @JsonProperty("expiresIn")
    private long expiresIn;
}