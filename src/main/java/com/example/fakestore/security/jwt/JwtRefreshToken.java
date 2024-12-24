package com.example.fakestore.security.jwt;

import com.example.fakestore.entity.RefreshToken;
import com.example.fakestore.entity.User;
import com.example.fakestore.exception.ApiException;
import com.example.fakestore.exception.ErrorCode;
import com.example.fakestore.repository.RefreshTokenRepository;
import com.example.fakestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtRefreshToken {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.refresh-token-expiration-mil}")
    private Long refreshTokenExpirationMs;

    // generation refresh token from email
    public RefreshToken generateRefreshToken(String email) {
        User existingUser = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: " + email);
                    throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "User not found");
                });

        RefreshToken refreshToken = existingUser.getRefreshToken();

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshTokenString(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenExpirationMs))
                    .user(existingUser)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    // verify the refresh token
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken existingRefreshToken = refreshTokenRepository
                .findByRefreshTokenString(refreshToken)
                .orElseThrow(() -> {
                    log.error("Refresh token not found");
                    throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "Refresh token not found");
                });

        if (existingRefreshToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(existingRefreshToken);
            throw new ApiException(ErrorCode.BAD_REQUEST, "Refresh token expired");
        }

        return existingRefreshToken;
    }
}
