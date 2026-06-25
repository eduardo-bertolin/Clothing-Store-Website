package com.fhcs.clothing_store.application.port.in.service;

import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.UserPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RegisterRequest;

public interface UserServicePort {
    UserBO createUser(RegisterRequest request);
    UserBO getUserByToken(String accessToken) throws Exception;
    UserBO updateUser(String accessToken, UserPatchDto patch) throws Exception;
    void deleteUser(String accessToken) throws Exception;
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
