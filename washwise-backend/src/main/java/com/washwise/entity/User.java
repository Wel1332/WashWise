package com.washwise.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_users_email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user
     * UUID is generated automatically by PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * User email - must be unique
     * Used for login and identification
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Hashed password using BCrypt
     * NEVER store plaintext passwords
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's full name
     */
    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 255)
    private String fullName;

    /**
     * User role: ADMIN, STAFF, or CUSTOMER
     * Determines access levels and permissions
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /**
     * Account creation timestamp
     * Set automatically when record is created
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Account last update timestamp
     * Updated automatically on any modification
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ============================================
    // RELATIONSHIPS - CASCADE DELETE
    // ============================================

    /**
     * Refresh tokens associated with this user
     * When user is deleted, all refresh tokens are automatically deleted
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    /**
     * Orders created by this user
     * When user is deleted, all orders are automatically deleted
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders = new ArrayList<>();

    /**
     * Reviews written by this user
     * When user is deleted, all reviews are automatically deleted
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews = new ArrayList<>();

    /**
     * User profile information
     * When user is deleted, profile is automatically deleted
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserProfile userProfile;

    // ============================================
    // CONVENIENCE METHODS
    // ============================================

    /**
     * Check if user has a specific role
     * Convenience method for role checking
     */
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * Check if user is staff
     */
    public boolean isStaff() {
        return this.role == UserRole.STAFF;
    }

    /**
     * Check if user is customer
     */
    public boolean isCustomer() {
        return this.role == UserRole.CUSTOMER;
    }
}