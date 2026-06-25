package com.fhcs.clothing_store.application.port.out;

import java.time.Instant;
import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.RefreshTokenBO;

public interface RefreshTokenRepositoryPort {
    RefreshTokenBO save(RefreshTokenBO token);
    Optional<RefreshTokenBO> findByToken(String token);
    void revokeToken(String token, Instant revokedAt);
    void delete(RefreshTokenBO token);
}
