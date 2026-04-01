package com.washwise.service;

import com.washwise.dto.request.UserProfileRequest;
import com.washwise.dto.response.UserProfileResponse;
import com.washwise.entity.User;
import com.washwise.entity.UserProfile;
import com.washwise.repository.UserProfileRepository;
import com.washwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to convert DB bytes to a Base64 String for the frontend
    private String getBase64Image(UserProfile profile) {
        if (profile.getProfileImage() != null && profile.getProfileImage().length > 0) {
            String contentType = profile.getProfileImageContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/jpeg"; // Fallback
            }
            return "data:" + contentType + ";base64," +
                    Base64.getEncoder().encodeToString(profile.getProfileImage());
        }
        return null;
    }

    // Helper method to safely map Entity to DTO
    private UserProfileResponse mapToResponse(User user, UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId() != null ? profile.getId().toString() : null)
                .userId(user.getId().toString())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .bio(profile.getBio())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .city(profile.getCity())
                .zipCode(profile.getZipCode())
                .profileImageBase64(getBase64Image(profile)) // <-- Ensures Base64 is injected!
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().user(user).build()));

        return mapToResponse(user, profile);
    }

    public UserProfileResponse updateUserProfile(UUID userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().user(user).build()));

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getZipCode() != null) profile.setZipCode(request.getZipCode());
        
        profile.setUpdatedAt(LocalDateTime.now());
        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToResponse(user, updatedProfile);
    }

    public UserProfileResponse uploadProfileImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().user(user).build()));

        // Convert the file directly to bytes and save to the database
        profile.setProfileImage(file.getBytes());
        profile.setProfileImageContentType(file.getContentType());
        profile.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return mapToResponse(user, updatedProfile);
    }

    @Transactional
    public void deleteUserAccount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete the associated profile first
        userProfileRepository.findByUser(user).ifPresent(profile -> {
            userProfileRepository.delete(profile);
        });

        // Delete the user account
        userRepository.delete(user);
    }
}