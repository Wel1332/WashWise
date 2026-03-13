package com.washwise.repository;

import com.washwise.entity.User;
import com.washwise.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity
 * Provides database operations for User records
 * 
 * JpaRepository provides CRUD operations automatically:
 * - save(user)
 * - findById(id)
 * - findAll()
 * - delete(user)
 * - deleteById(id)
 * etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email address
     * Used for login and checking if email exists
     * 
     * @param email the user's email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * Useful for checking duplicate emails during registration
     * 
     * @param email the email to check
     * @return true if user exists with this email
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with a specific role
     * Useful for listing all staff members or admins
     * 
     * @param role the role to filter by (ADMIN, STAFF, CUSTOMER)
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find all staff members (users with STAFF role)
     * Used for staff dashboard and assignment
     * 
     * @return list of all staff users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STAFF'")
    List<User> findAllStaff();

    /**
     * Find all admin users
     * 
     * @return list of all admin users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();

    /**
     * Find all customers
     * 
     * @return list of all customer users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER'")
    List<User> findAllCustomers();

    /**
     * Search users by full name (case-insensitive)
     * Used for admin user management
     * 
     * @param fullName the name to search for
     * @return list of users matching the name
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<User> searchByFullName(@Param("fullName") String fullName);

    /**
     * Find user by email with custom query
     * Alternative to default findByEmail (demonstrates @Query)
     * 
     * @param email the email to find
     * @return Optional containing user if found
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    /**
     * Count users by role
     * Useful for statistics/dashboards
     * 
     * @param role the role to count
     * @return number of users with this role
     */
    long countByRole(UserRole role);
}
