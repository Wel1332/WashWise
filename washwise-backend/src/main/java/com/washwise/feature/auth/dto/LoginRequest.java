package com.washwise.feature.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login request
 * Received from client during sign-in
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /**
     * User's email address
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * User's password
     * Will be compared with stored password hash
     */
    @NotBlank(message = "Password is required")
    private String password;
}