package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.cart.CartRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartStatusBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.PrivateIndividual;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.Cart;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.cart.CartStatus;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.PrivateIndividualRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.cart.CartRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.CartEntityMapper;

@Component
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartRepository cartRepository;
    private final CartEntityMapper mapper;
    private final PrivateIndividualRepository individualRepository;

    public CartRepositoryAdapter(CartRepository cartRepository, CartEntityMapper mapper,
            PrivateIndividualRepository individualRepository) {
        this.cartRepository = cartRepository;
        this.mapper = mapper;
        this.individualRepository = individualRepository;
    }

    @Override
    public CartBO save(CartBO bo) {
        Cart entity;
        if (bo.getCartId() != null) {
            entity = cartRepository.findById(bo.getCartId()).orElse(new Cart());
        } else {
            entity = new Cart();
        }
        PrivateIndividual individual =
                individualRepository.findById(bo.getIndividualId()).orElseThrow();
        entity.setIndividual(individual);
        entity.setStatus(mapper.statusToEntity(bo.getStatus()));
        if (bo.getCreatedAt() != null) entity.setCreatedAt(bo.getCreatedAt());
        if (bo.getUpdatedAt() != null) entity.setUpdatedAt(bo.getUpdatedAt());
        return mapper.cartToBO(cartRepository.save(entity));
    }

    @Override
    public Optional<CartBO> findById(Integer cartId) {
        return cartRepository.findById(cartId).map(mapper::cartToBO);
    }

    @Override
    public Optional<CartBO> findByIndividualIdAndStatus(Integer individualId,
            CartStatusBO status) {
        CartStatus entityStatus = mapper.statusToEntity(status);
        return cartRepository
                .findByIndividual_IndividualIdAndStatus(individualId, entityStatus)
                .map(mapper::cartToBO);
    }
}
