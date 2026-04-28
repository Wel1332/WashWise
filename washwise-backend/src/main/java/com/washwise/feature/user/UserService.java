package com.washwise.feature.user;

import com.washwise.feature.user.dto.UserResponse;
import com.washwise.feature.user.entity.User;
import com.washwise.feature.user.entity.UserRole;
import com.washwise.feature.user.repository.UserRepository;
import com.washwise.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUserRole(String id, String roleName) {
        UserRole role = parseRole(roleName);
        User user = userRepository.findById(parseUuid(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("User {} role updated to {}", saved.getEmail(), role);
        return mapToResponse(saved);
    }

    private UserRole parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        try {
            return UserRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + value);
        }
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new ResourceNotFoundException("Invalid user id: " + value);
        }
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
