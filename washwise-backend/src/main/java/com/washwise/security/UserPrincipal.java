package com.washwise.security;

import com.washwise.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation
 * Wraps User entity to make it compatible with Spring Security
 * 
 * Spring Security needs UserDetails interface for authentication
 * This class adapts our User entity to that interface
 */
@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Get user's authorities/roles
     * Spring Security uses this for permission checks
     * 
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert user role to GrantedAuthority
        // Format: "ROLE_" prefix is Spring Security convention
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    /**
     * Get user password (hashed)
     * Used during authentication
     * 
     * @return password hash
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Get username (email in our case)
     * Used as principal identifier
     * 
     * @return user email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Check if account is not expired
     * We don't implement account expiration, so always true
     * 
     * @return true (account never expires)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Check if account is not locked
     * We don't implement account locking, so always true
     * 
     * @return true (account never locked)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Check if credentials are not expired
     * We don't implement credential expiration, so always true
     * 
     * @return true (credentials never expire)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Check if user is enabled
     * We don't implement user enabling/disabling, so always true
     * 
     * @return true (user always enabled)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}