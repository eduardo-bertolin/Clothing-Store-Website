package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.AuthResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.UserDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.UserResponse;

@Component
public class UserDtoMapper {

    public UserDto toUserDto(UserBO bo) {
        return UserDto.builder()
                .userId(bo.getUserId())
                .username(bo.getUsername())
                .email(bo.getEmail())
                .build();
    }

    public AuthResponse toAuthResponse(String accessToken, String refreshToken,
            long expiresIn, UserBO user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .safeUser(toUserDto(user))
                .success(true)
                .build();
    }

    public UserResponse toUserResponse(UserBO user, String message) {
        return UserResponse.builder()
                .success(true)
                .message(message)
                .safeUser(toUserDto(user))
                .build();
    }

    public AuthResponse toAuthError(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public UserResponse toUserError(String message) {
        return UserResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
