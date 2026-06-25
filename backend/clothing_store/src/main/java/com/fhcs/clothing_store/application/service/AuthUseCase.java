package com.fhcs.clothing_store.application.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.AuthServicePort;
import com.fhcs.clothing_store.application.port.in.service.RefreshTokenServicePort;
import com.fhcs.clothing_store.application.port.in.service.UserServicePort;
import com.fhcs.clothing_store.application.port.out.JwtPort;
import com.fhcs.clothing_store.application.port.out.PasswordEncoderPort;
import com.fhcs.clothing_store.application.port.out.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.AuthResultBO;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.core.domain.exception.InvalidTokenException;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.LoginRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RegisterRequest;

@Service
@Transactional
public class AuthUseCase implements AuthServicePort {

    private final UserServicePort userService;
    private final RefreshTokenServicePort refreshTokenService;
    private final UserRepositoryPort userRepository;
    private final JwtPort jwtPort;
    private final PasswordEncoderPort passwordEncoder;

    public AuthUseCase(UserServicePort userService, RefreshTokenServicePort refreshTokenService,
            UserRepositoryPort userRepository, JwtPort jwtPort, PasswordEncoderPort passwordEncoder) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.jwtPort = jwtPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResultBO register(RegisterRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Usuário já existe");
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Usuário já existe");
        }

        UserBO user = userService.createUser(request);
        return buildAuthResult(user);
    }

    @Override
    public AuthResultBO login(LoginRequest request) {
        try {
            UserBO user = authenticateUser(request.getEmail(), request.getPassword());
            return buildAuthResult(user);
        } catch (Exception e) {
            return AuthResultBO.error(e.getMessage());
        }
    }

    @Override
    public AuthResultBO refresh(String refreshToken) {
        try {
            refreshTokenService.validateRefreshToken(refreshToken);
            String username = jwtPort.extractUsername(refreshToken);
            UserBO user = userRepository.findByUsername(username);
            if (user == null) {
                throw new InvalidTokenException("Usuário não encontrado");
            }
            List<String> roles = extractRoleNames(user);
            String newAccessToken = jwtPort.generateAccessToken(user.getUsername(), roles);
            long expiresIn = jwtPort.getAccessTokenExpiration(newAccessToken);
            return AuthResultBO.success(newAccessToken, refreshToken, expiresIn, user);
        } catch (Exception e) {
            throw new InvalidTokenException("Erro: " + e.getMessage());
        }
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.revokeToken(refreshToken);
        }
    }

    private UserBO authenticateUser(String email, String password) {
        UserBO user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        return user;
    }

    private AuthResultBO buildAuthResult(UserBO user) {
        List<String> roles = extractRoleNames(user);
        String accessToken = jwtPort.generateAccessToken(user.getUsername(), roles);
        String refreshToken = jwtPort.generateRefreshToken(user.getUsername(), roles);
        refreshTokenService.createRefreshToken(user, refreshToken);
        long expiresIn = jwtPort.getAccessTokenExpiration(accessToken);
        return AuthResultBO.success(accessToken, refreshToken, expiresIn, user);
    }

    private List<String> extractRoleNames(UserBO user) {
        return user.getRoles().stream()
                .map(r -> r.getRoleName())
                .collect(Collectors.toList());
    }
}
