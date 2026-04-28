package com.washwise.feature.user;

import com.washwise.feature.user.dto.UserResponse;
import com.washwise.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User administration endpoints")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users", description = "Admin-only view of every registered user")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.debug("GET /users - admin listing all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved successfully", HttpStatus.OK.value()));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Promote or demote a user (ADMIN/STAFF/CUSTOMER)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable String id,
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid UpdateRoleRequest request) {
        log.info("PUT /users/{}/role - new role: {}", id, request.getRole());
        UserResponse user = userService.updateUserRole(id, request.getRole());
        return ResponseEntity.ok(
                ApiResponse.success(user, "User role updated successfully", HttpStatus.OK.value()));
    }

    @Data
    public static class UpdateRoleRequest {
        @NotBlank(message = "Role is required")
        private String role;
    }
}
