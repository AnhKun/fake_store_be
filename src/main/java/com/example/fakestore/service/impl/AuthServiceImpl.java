package com.example.fakestore.service.impl;

import com.example.fakestore.dto.request.LoginDto;
import com.example.fakestore.dto.request.LogoutRequest;
import com.example.fakestore.dto.request.RefreshTokenRequest;
import com.example.fakestore.dto.request.RegisterDto;
import com.example.fakestore.dto.response.ApiResponse;
import com.example.fakestore.dto.response.JwtAuthResponse;
import com.example.fakestore.entity.InvalidatedToken;
import com.example.fakestore.entity.RefreshToken;
import com.example.fakestore.entity.Role;
import com.example.fakestore.entity.User;
import com.example.fakestore.exception.ApiException;
import com.example.fakestore.exception.ErrorCode;
import com.example.fakestore.repository.InvalidatedTokenRepository;
import com.example.fakestore.repository.UserRepository;
import com.example.fakestore.security.SecurityConfig;
import com.example.fakestore.security.jwt.JwtRefreshToken;
import com.example.fakestore.security.jwt.JwtTokenProvider;
import com.example.fakestore.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRefreshToken jwtRefreshToken;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public ApiResponse register(RegisterDto registerDto) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(registerDto.getEmail()))) {
            log.error("Email {} already exists", registerDto.getEmail());
            throw new ApiException(ErrorCode.BAD_REQUEST, "Email already exists");
        }

        User newUser = User.builder()
                .email(registerDto.getEmail())
                .password(SecurityConfig.passwordEncoder().encode(registerDto.getPassword()))
                .role(Role.ROLE_CUSTOMER)
                .build();

        userRepository.save(newUser);

        return ApiResponse.builder()
                .code(HttpStatus.CREATED)
                .result("User registered successfully")
                .build();
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken =
                jwtRefreshToken.generateRefreshToken(loginDto.getEmail()).getRefreshTokenString();

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refToken = jwtRefreshToken.verifyRefreshToken(refreshTokenRequest.getRefreshToken());

        User user = refToken.getUser();

        String accessToken = jwtTokenProvider.generateTokenFromUser(user);

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        Claims claims = jwtTokenProvider.getJwtTokenClaims(request.getToken());

        if (!(jwtTokenProvider.validateToken(request.getToken())
                && claims.getExpiration().after(new Date()))) {
            throw new ApiException(ErrorCode.UNAUTHENTICATED);
        }

        String uuid = claims.get("uuid", String.class);
        Date exp = claims.getExpiration();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(uuid).expiryTime(exp).build();

        invalidatedTokenRepository.save(invalidatedToken);
    }
}
