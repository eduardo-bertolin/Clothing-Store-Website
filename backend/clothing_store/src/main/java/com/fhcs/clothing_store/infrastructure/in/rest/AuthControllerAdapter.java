package com.fhcs.clothing_store.infrastructure.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.AuthServicePort;
import com.fhcs.clothing_store.core.domain.bo.AuthResultBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.LoginRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.LogoutRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RefreshTokenRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RegisterRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.AuthResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.UserDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthControllerAdapter {

    private final AuthServicePort authService;
    private final UserDtoMapper userMapper;

    public AuthControllerAdapter(AuthServicePort authService, UserDtoMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResultBO result = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userMapper.toAuthResponse(result.getAccessToken(), result.getRefreshToken(),
                            result.getExpiresIn(), result.getUser()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(userMapper.toAuthError("Credenciais Inválidas " + ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResultBO result = authService.login(request);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(userMapper.toAuthError(result.getMessage()));
        }
        return ResponseEntity.ok(userMapper.toAuthResponse(result.getAccessToken(),
                result.getRefreshToken(), result.getExpiresIn(), result.getUser()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResultBO result = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(userMapper.toAuthResponse(result.getAccessToken(),
                result.getRefreshToken(), result.getExpiresIn(), result.getUser()));
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
    }
}
