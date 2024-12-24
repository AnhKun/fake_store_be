package com.example.fakestore.security.jwt;

import com.example.fakestore.entity.User;
import com.example.fakestore.exception.ApiException;
import com.example.fakestore.exception.ErrorCode;
import com.example.fakestore.repository.InvalidatedTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-mil}")
    private Long jwtExpirationDate;

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    // generate JWT token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                .claim("uuid", UUID.randomUUID().toString())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key(jwtSecret))
                .compact();

        return token;
    }

    // generate JWT token from User entity
    public String generateTokenFromUser(User user) {
        String email = user.getEmail();

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .claim("uuid", UUID.randomUUID().toString())
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key(jwtSecret))
                .compact();
    }

    private Key key(String secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // get username from JWT token
    public String getEmailFromToken(String token) {
        return getJwtTokenClaims(token).getSubject();
    }

    public Claims getJwtTokenClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key(jwtSecret))
                .build()
                .parseSignedClaims(token)
                .getBody();

        return claims;
    }

    // validate Jwt token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key(jwtSecret)).build().parse(token);

            Claims claims = getJwtTokenClaims(token);
            if (invalidatedTokenRepository.existsById(claims.get("uuid", String.class))) {
                throw new ApiException(ErrorCode.BAD_REQUEST, "You already logged out!");
            }

            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
