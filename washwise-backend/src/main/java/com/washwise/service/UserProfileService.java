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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    // ✅ Changed parameter from String to UUID
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Changed to findByUser instead of findByUserId
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .build();
                    return userProfileRepository.save(newProfile);
                });

        return new UserProfileResponse(
                profile.getId().toString(),
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().toString(),
                profile.getBio(),
                profile.getPhoneNumber(),
                profile.getAddress(),
                profile.getCity(),
                profile.getZipCode(),
                profile.getProfileImageUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    // ✅ Changed parameter from String to UUID
    public UserProfileResponse updateUserProfile(UUID userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Changed to findByUser instead of findByUserId
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .build();
                    return userProfileRepository.save(newProfile);
                });

        // ✅ Only update if values are not null
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getZipCode() != null) {
            profile.setZipCode(request.getZipCode());
        }
        
        profile.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return new UserProfileResponse(
                updatedProfile.getId().toString(),
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().toString(),
                updatedProfile.getBio(),
                updatedProfile.getPhoneNumber(),
                updatedProfile.getAddress(),
                updatedProfile.getCity(),
                updatedProfile.getZipCode(),
                updatedProfile.getProfileImageUrl(),
                updatedProfile.getCreatedAt(),
                updatedProfile.getUpdatedAt()
        );
    }

    // ✅ Changed parameter from String to UUID
    public UserProfileResponse uploadProfileImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Changed to findByUser instead of findByUserId
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder()
                            .user(user)
                            .build();
                    return userProfileRepository.save(newProfile);
                });

        // Delete old image if exists
        if (profile.getProfileImageUrl() != null) {
            imageService.deleteImage(profile.getProfileImageUrl());
        }

        // Upload new image
        String imageUrl = imageService.uploadImage(file);
        profile.setProfileImageUrl(imageUrl);
        profile.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return new UserProfileResponse(
                updatedProfile.getId().toString(),
                user.getId().toString(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().toString(),
                updatedProfile.getBio(),
                updatedProfile.getPhoneNumber(),
                updatedProfile.getAddress(),
                updatedProfile.getCity(),
                updatedProfile.getZipCode(),
                updatedProfile.getProfileImageUrl(),
                updatedProfile.getCreatedAt(),
                updatedProfile.getUpdatedAt()
        );
    }
}