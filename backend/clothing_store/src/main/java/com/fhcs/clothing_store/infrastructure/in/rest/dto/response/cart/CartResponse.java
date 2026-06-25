package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart;
 
import lombok.Builder;
import lombok.Getter;
 
@Builder
@Getter
public class CartResponse {
 
    private boolean success;
    private String message;
    private CartDto cart;
 
    public static CartResponse success(CartDto cart, String message) {
        return CartResponse.builder().success(true).message(message).cart(cart).build();
    }

    public static CartResponse successBO(com.fhcs.clothing_store.core.domain.bo.cart.CartBO cart, String message) {
        return CartResponse.builder().success(true).message(message).cart(CartDto.fromBO(cart)).build();
    }
 
    public static CartResponse error(String message) {
        return CartResponse.builder().success(false).message(message).build();
    }
}