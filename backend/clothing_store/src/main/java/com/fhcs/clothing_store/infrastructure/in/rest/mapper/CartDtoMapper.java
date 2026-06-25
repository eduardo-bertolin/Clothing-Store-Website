package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart.CartDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart.CartItemDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart.CartResponse;

@Component
public class CartDtoMapper {

    public CartItemDto toCartItemDto(CartItemBO item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemDto.builder()
                .cartItemId(item.getCartItemId())
                .variationId(item.getVariation().getVariationId())
                .productName(item.getVariation().getProduct().getName())
                .color(item.getVariation().getColor().getColor())
                .size(item.getVariation().getSize().getSize())
                .skuCode(item.getVariation().getSkuCode())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .availableStock(item.getVariation().getStock())
                .build();
    }

    public CartDto toCartDto(CartBO cart) {
        List<CartItemDto> items = cart.getItems().stream()
                .map(this::toCartItemDto)
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartDto.builder()
                .cartId(cart.getCartId())
                .status(cart.getStatus().name())
                .items(items)
                .total(total)
                .itemCount(items.size())
                .build();
    }

    public CartResponse toCartResponse(CartBO cart, String message) {
        return CartResponse.builder()
                .success(true)
                .message(message)
                .cart(toCartDto(cart))
                .build();
    }

    public CartResponse toCartError(String message) {
        return CartResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
