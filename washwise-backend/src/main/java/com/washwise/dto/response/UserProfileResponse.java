package com.washwise.dto.response;

import java.time.LocalDateTime;

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
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor
    public UserProfileResponse(
            String id, String userId, String fullName, String email, String role,
            String bio, String phoneNumber, String address, String city, String zipCode,
            String profileImageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBio() { return bio; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}