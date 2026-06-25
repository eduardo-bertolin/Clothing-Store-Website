package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.cart.CartItemRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.Cart;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.CartItem;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.ProductVariation;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.cart.CartItemRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.cart.CartRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation.ProductVariationRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.CartEntityMapper;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class CartItemRepositoryAdapter implements CartItemRepositoryPort {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductVariationRepository variationRepository;
    private final CartEntityMapper cartMapper;
    private final ProductEntityMapper productMapper;

    public CartItemRepositoryAdapter(CartItemRepository cartItemRepository,
            CartRepository cartRepository, ProductVariationRepository variationRepository,
            CartEntityMapper cartMapper, ProductEntityMapper productMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.variationRepository = variationRepository;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public CartItemBO save(CartItemBO bo) {
        CartItem entity;
        if (bo.getCartItemId() != null) {
            entity = cartItemRepository.findById(bo.getCartItemId()).orElse(new CartItem());
        } else {
            entity = new CartItem();
        }
        Cart cart = cartRepository.findById(bo.getCartId()).orElseThrow();
        ProductVariation variation =
                variationRepository.findById(bo.getVariation().getVariationId()).orElseThrow();
        entity.setCart(cart);
        entity.setVariation(variation);
        entity.setQuantity(bo.getQuantity());
        entity.setUnitPrice(bo.getUnitPrice());
        return cartMapper.cartItemToBO(cartItemRepository.save(entity));
    }

    @Override
    public Optional<CartItemBO> findById(Integer cartItemId) {
        return cartItemRepository.findById(cartItemId).map(cartMapper::cartItemToBO);
    }

    @Override
    public Optional<CartItemBO> findByCartIdAndVariationId(Integer cartId, Integer variationId) {
        return cartItemRepository
                .findByCart_CartIdAndVariation_VariationId(cartId, variationId)
                .map(cartMapper::cartItemToBO);
    }

    @Override
    public void delete(CartItemBO bo) {
        cartItemRepository.deleteById(bo.getCartItemId());
    }
}
