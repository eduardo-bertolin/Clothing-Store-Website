package com.fhcs.clothing_store.infrastructure.in.rest.dto.request;

import lombok.Getter;

@Getter
public class LogoutRequest {
    
    private String refreshToken;
}
