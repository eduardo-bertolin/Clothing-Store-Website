package com.fhcs.clothing_store.application.port.out;

import java.util.List;

public interface JwtPort {
    String generateAccessToken(String username, List<String> roles);
    String generateRefreshToken(String username, List<String> roles);
    String extractUsername(String token);
    boolean validateToken(String token, String username);
    long getAccessTokenExpiration(String token);
}
