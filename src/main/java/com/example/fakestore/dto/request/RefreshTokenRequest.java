package com.example.fakestore.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshTokenRequest {
    @NotEmpty(message = "Please provide the refresh token")
    private String refreshToken;
}
