package com.fhcs.clothing_store.application.port.out.cart;

import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartStatusBO;

public interface CartRepositoryPort {
    CartBO save(CartBO cart);
    Optional<CartBO> findById(Integer cartId);
    Optional<CartBO> findByIndividualIdAndStatus(Integer individualId, CartStatusBO status);
}
