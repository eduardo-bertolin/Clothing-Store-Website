package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartStatusBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.Cart;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.CartItem;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.CartStatus;

@Component
public class CartEntityMapper {

    private final ProductEntityMapper productMapper;

    public CartEntityMapper(ProductEntityMapper productMapper) {
        this.productMapper = productMapper;
    }

    public CartStatusBO statusToBO(CartStatus status) {
        return CartStatusBO.valueOf(status.name());
    }

    public CartStatus statusToEntity(CartStatusBO status) {
        return CartStatus.valueOf(status.name());
    }

    public CartItemBO cartItemToBO(CartItem entity) {
        if (entity == null) return null;
        return CartItemBO.builder()
                .cartItemId(entity.getCartItemId())
                .cartId(entity.getCart().getCartId())
                .variation(productMapper.variationToBO(entity.getVariation()))
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .build();
    }

    public CartBO cartToBO(Cart entity) {
        if (entity == null) return null;
        return CartBO.builder()
                .cartId(entity.getCartId())
                .individualId(entity.getIndividual().getIndividualId())
                .status(statusToBO(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(entity.getItems().stream()
                        .map(this::cartItemToBO)
                        .collect(Collectors.toList()))
                .build();
    }
}
