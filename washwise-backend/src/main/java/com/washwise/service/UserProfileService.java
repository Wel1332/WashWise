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

    public UserProfileResponse getUserProfile(String userId) {
        // Convert userId String to UUID
        UUID userUUID = UUID.fromString(userId);
        
        User user = userRepository.findById(userUUID)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userUUID)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile(user);
                    return userProfileRepository.save(newProfile);
                });

        return new UserProfileResponse(
                profile.getId(),
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

    public UserProfileResponse updateUserProfile(String userId, UserProfileRequest request) {
        // Convert userId String to UUID
        UUID userUUID = UUID.fromString(userId);
        
        User user = userRepository.findById(userUUID)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userUUID)
                .orElseGet(() -> new UserProfile(user));

        profile.setBio(request.getBio());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setZipCode(request.getZipCode());
        profile.setUpdatedAt(LocalDateTime.now());

        UserProfile updatedProfile = userProfileRepository.save(profile);

        return new UserProfileResponse(
                updatedProfile.getId(),
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

    public UserProfileResponse uploadProfileImage(String userId, MultipartFile file) throws IOException {
        // Convert userId String to UUID
        UUID userUUID = UUID.fromString(userId);
        
        User user = userRepository.findById(userUUID)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(userUUID)
                .orElseGet(() -> new UserProfile(user));

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
                updatedProfile.getId(),
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