package com.fhcs.clothing_store.application.port.in.service;

import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CartItemRequest;

public interface CartServicePort {
    CartBO getOrCreateCart(String accessToken);
    CartBO addItem(String accessToken, CartItemRequest request);
    CartBO updateItemQuantity(String accessToken, Integer cartItemId, Integer quantity);
    CartBO removeItem(String accessToken, Integer cartItemId);
    CartBO clearCart(String accessToken);
}
