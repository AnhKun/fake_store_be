package com.example.fakestore.repository;

import com.example.fakestore.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Query("SELECT r FROM RefreshToken r WHERE r.refreshTokenString = :refreshToken")
    Optional<RefreshToken> findByRefreshTokenString(String refreshToken);
}
