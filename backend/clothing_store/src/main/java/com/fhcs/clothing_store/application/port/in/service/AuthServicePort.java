package com.fhcs.clothing_store.application.port.in.service;

import com.fhcs.clothing_store.core.domain.bo.AuthResultBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.LoginRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RegisterRequest;

public interface AuthServicePort {
    AuthResultBO register(RegisterRequest request);
    AuthResultBO login(LoginRequest request);
    AuthResultBO refresh(String refreshToken);
    void logout(String refreshToken);
}
