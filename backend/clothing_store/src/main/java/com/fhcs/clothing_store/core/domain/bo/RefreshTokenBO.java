package com.fhcs.clothing_store.core.domain.bo;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenBO {
    private Integer id;
    private String token;
    private UserBO user;
    private Instant expiryDate;
    private boolean revoked;
    private Instant createdAt;
    private Instant revokedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}
