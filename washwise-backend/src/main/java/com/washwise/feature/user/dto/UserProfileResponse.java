package com.washwise.feature.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String userId;
    private String fullName;
    private String email;
    private String role;
    private String bio;
    private String phoneNumber;
    private String address;
    private String city;
    private String zipCode;
    private String profileImageBase64;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}