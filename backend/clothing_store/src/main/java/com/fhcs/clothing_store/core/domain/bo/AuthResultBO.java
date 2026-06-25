package com.fhcs.clothing_store.core.domain.bo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResultBO {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserBO user;
    private boolean success;
    private String message;

    public static AuthResultBO success(String accessToken, String refreshToken, long expiresIn, UserBO user) {
        return AuthResultBO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .user(user)
                .success(true)
                .build();
    }

    public static AuthResultBO error(String message) {
        return AuthResultBO.builder()
                .success(false)
                .message(message)
                .build();
    }
}
