package com.washwise.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT (JSON Web Token) Service
 * Handles token generation, validation, and claim extraction
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${app.jwt.secret:your-secret-key-minimum-32-characters-required}")
    private String secretKey;
    
    @Value("${app.jwt.expiry-ms:900000}")
    private long jwtExpiryMs;

    @Value("${app.jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    /**
     * Generate a SecretKey from the JWT secret string
     * Used for signing and validating tokens
     * 
     * @return SecretKey for HMAC SHA-256
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT access token for a user
     * Token includes email and expires in specified time
     * 
     * @param email user email
     * @return JWT token string
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, jwtExpiryMs);
    }

    /**
     * Generate JWT access token with custom claims
     * Allows adding extra information to the token
     * 
     * @param extraClaims additional claims to include
     * @param email user email
     * @return JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return createToken(extraClaims, email, jwtExpiryMs);
    }

    /**
     * Generate JWT access token for UserDetails
     * 
     * @param userDetails the authenticated user
     * @return JWT token string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtExpiryMs);
    }

    /**
     * Generate refresh token for a user
     * Refresh tokens live longer than access tokens
     * Used to obtain new access tokens without re-login
     * 
     * @param email user email
     * @return refresh token string
     */
    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email, refreshTokenExpiryMs);
    }

    /**
     * Create a JWT token with specified claims and expiration
     * Core token creation logic using JJWT API
     * 
     * @param claims token claims (custom data)
     * @param subject token subject (usually username/email)
     * @param expiration expiration time in milliseconds
     * @return signed JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiresAt = new Date(now + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username/email from token
     * Used to identify which user made the request
     * 
     * @param token JWT token
     * @return username from token subject claim
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     * Used to check if token is expired
     * 
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic method to extract any claim from token
     * Uses Function parameter for flexibility
     * 
     * @param token JWT token
     * @param claimsResolver function to extract specific claim
     * @param <T> type of claim value
     * @return claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     * Validates signature during extraction using JJWT API
     * 
     * @param token JWT token
     * @return all claims in the token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if token expiration date is before now
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true; // Treat invalid tokens as expired
        }
    }

    /**
     * Validate token against user details
     * Checks if token belongs to the user and is not expired
     * 
     * @param token JWT token
     * @param userDetails the user to validate against
     * @return true if token is valid for this user
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Validate token (basic check)
     * Just checks if token is expired and signature is valid
     * 
     * @param token JWT token
     * @return true if token is valid
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get token expiration time in milliseconds
     * Useful for frontend to know when to refresh
     * 
     * @return milliseconds until token expires
     */
    public long getExpirationTime() {
        return jwtExpiryMs;
    }

    /**
     * Get remaining time until token expires
     * 
     * @param token JWT token
     * @return milliseconds until expiration
     */
    public long getTokenExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
}