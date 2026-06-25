package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fhcs.clothing_store.application.port.out.RefreshTokenRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.RefreshTokenBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.RefreshToken;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.RefreshTokenRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.UserEntityMapper;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserEntityMapper userMapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenRepository refreshTokenRepository,
            UserEntityMapper userMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userMapper = userMapper;
    }

    @Override
    public RefreshTokenBO save(RefreshTokenBO bo) {
        RefreshToken entity = RefreshToken.builder()
                .id(bo.getId())
                .token(bo.getToken())
                .user(userMapper.toEntity(bo.getUser()))
                .expiryDate(bo.getExpiryDate())
                .revoked(bo.isRevoked())
                .createdAt(bo.getCreatedAt())
                .build();
        RefreshToken saved = refreshTokenRepository.save(entity);
        return toBO(saved);
    }

    @Override
    public Optional<RefreshTokenBO> findByToken(String token) {
        return refreshTokenRepository.findByToken(token).map(this::toBO);
    }

    @Override
    @Transactional
    public void revokeToken(String token, Instant revokedAt) {
        refreshTokenRepository.revokeToken(token, revokedAt);
    }

    @Override
    public void delete(RefreshTokenBO bo) {
        RefreshToken entity = refreshTokenRepository.findByToken(bo.getToken())
                .orElseThrow();
        refreshTokenRepository.delete(entity);
    }

    private RefreshTokenBO toBO(RefreshToken entity) {
        return RefreshTokenBO.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .user(userMapper.toBO(entity.getUser()))
                .expiryDate(entity.getExpiryDate())
                .revoked(entity.isRevoked())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
