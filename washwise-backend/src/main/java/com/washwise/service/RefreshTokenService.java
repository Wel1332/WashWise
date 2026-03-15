package com.washwise.service;

import com.washwise.entity.RefreshToken;
import com.washwise.entity.User;
import com.washwise.exception.ResourceNotFoundException;
import com.washwise.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiry-ms:604800000}")
    private long refreshTokenExpiryMs;

    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user: {}", user.getEmail());

        // Delete existing refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);
        
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(LocalDateTime.now().plusNanos(refreshTokenExpiryMs * 1_000_000))
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        log.debug("Validating refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResourceNotFoundException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh  token");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshTokenRepository.delete(refreshToken);
    }
}
