package com.washwise.feature.user;

import com.washwise.feature.user.dto.UserProfileRequest;
import com.washwise.feature.user.dto.UserProfileResponse;
import com.washwise.feature.user.entity.User;
import com.washwise.feature.user.repository.UserRepository;
import com.washwise.shared.dto.ApiResponse;
import com.washwise.shared.exception.InvalidCredentialsException;
import com.washwise.shared.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile", description = "User profile and account endpoints")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Update password for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = currentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for {}", user.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Password changed successfully", HttpStatus.OK.value()));
    }

    @GetMapping
    @Operation(summary = "Get my profile", description = "Retrieve the profile of the authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        User user = currentUser();
        UserProfileResponse profile = userProfileService.getUserProfile(user.getId());
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile retrieved successfully", HttpStatus.OK.value()));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get public profile", description = "Retrieve another user's public profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getPublicProfile(@PathVariable String userId) {
        UserProfileResponse profile = userProfileService.getUserProfile(parseUuid(userId));
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping
    @Operation(summary = "Update my profile", description = "Update profile information for the authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        User user = currentUser();
        UserProfileResponse profile = userProfileService.updateUserProfile(user.getId(), request);
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile updated successfully", HttpStatus.OK.value()));
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Upload profile image", description = "Upload a profile image for the authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) throws java.io.IOException {
        User user = currentUser();
        UserProfileResponse profile = userProfileService.uploadProfileImage(user.getId(), file);
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile image uploaded successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/account")
    @Operation(summary = "Delete account", description = "Delete the authenticated user's account and related data")
    public ResponseEntity<ApiResponse<Void>> deleteAccount() {
        User user = currentUser();
        userProfileService.deleteUserAccount(user.getId());
        log.info("Account deleted for {}", user.getEmail());
        return ResponseEntity.ok(
                ApiResponse.success(null, "Account deleted successfully", HttpStatus.OK.value()));
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new InvalidCredentialsException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("Invalid user id: " + value);
        }
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        private String newPassword;
    }
}
