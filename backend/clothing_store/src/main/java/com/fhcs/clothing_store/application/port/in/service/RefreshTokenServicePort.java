package com.fhcs.clothing_store.application.port.in.service;

import com.fhcs.clothing_store.core.domain.bo.UserBO;

public interface RefreshTokenServicePort {
    void createRefreshToken(UserBO user, String token);
    void revokeToken(String token);
    void validateRefreshToken(String token);
}
