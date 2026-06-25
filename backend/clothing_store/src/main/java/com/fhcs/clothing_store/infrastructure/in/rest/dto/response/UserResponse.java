package com.fhcs.clothing_store.infrastructure.in.rest.dto.response;

import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private boolean success;
    private String message;
    private UserDto safeUser;

    public static UserResponse success(User user, String message) {
        return UserResponse.builder()
                .success(true)
                .message(message)
                .safeUser(UserDto.fromUser(user))
                .build();
    }

    public static UserResponse successBO(UserBO user, String message) {
        return UserResponse.builder()
                .success(true)
                .message(message)
                .safeUser(UserDto.fromBO(user))
                .build();
    }

    public static UserResponse error(String message) {
        return UserResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static UserResponse messageOnly(String message, boolean success) {
        return UserResponse.builder()
                .success(success)
                .message(message)
                .build();
    }
}
