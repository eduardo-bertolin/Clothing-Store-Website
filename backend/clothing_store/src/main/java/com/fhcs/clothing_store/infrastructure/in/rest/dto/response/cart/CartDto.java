package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart;

import java.math.BigDecimal;
import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.Cart;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartDto {

    private Integer cartId;
    private String status;
    private List<CartItemDto> items;
    private BigDecimal total;
    private Integer itemCount;

    public static CartDto fromCart(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(CartItemDto::fromCartItem)
                .toList();
        BigDecimal total = itemDtos.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartDto.builder()
                .cartId(cart.getCartId())
                .status(cart.getStatus().name())
                .items(itemDtos)
                .total(total)
                .itemCount(itemDtos.size())
                .build();
    }

    public static CartDto fromBO(CartBO cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(CartItemDto::fromBO)
                .toList();
        BigDecimal total = itemDtos.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartDto.builder()
                .cartId(cart.getCartId())
                .status(cart.getStatus().name())
                .items(itemDtos)
                .total(total)
                .itemCount(itemDtos.size())
                .build();
    }
}