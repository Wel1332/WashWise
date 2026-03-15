package com.washwise.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request
 * Received from client during sign-up
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /**
     * User's email address
     * Must be valid email format and unique
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    /**
     * User's password
     * Must be at least 8 characters
     * Should contain uppercase, lowercase, and numbers
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Password confirmation
     * Must match password field
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    /**
     * User's full name
     * Optional but recommended
     */
    private String fullName;
}