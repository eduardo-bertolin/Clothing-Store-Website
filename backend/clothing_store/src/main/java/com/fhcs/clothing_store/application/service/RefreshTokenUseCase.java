package com.fhcs.clothing_store.application.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhcs.clothing_store.application.port.in.service.RefreshTokenServicePort;
import com.fhcs.clothing_store.application.port.out.RefreshTokenRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.RefreshTokenBO;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.core.domain.exception.InvalidTokenException;

@Service
@Transactional
public class RefreshTokenUseCase implements RefreshTokenServicePort {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenDurationMs;

    public RefreshTokenUseCase(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void createRefreshToken(UserBO user, String token) {
        RefreshTokenBO refreshToken = RefreshTokenBO.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void revokeToken(String token) {
        refreshTokenRepository.revokeToken(token, Instant.now());
    }

    @Override
    public void validateRefreshToken(String token) {
        RefreshTokenBO refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh Token não encontrado"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh Token revogado");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh Token expirado");
        }
    }
}
