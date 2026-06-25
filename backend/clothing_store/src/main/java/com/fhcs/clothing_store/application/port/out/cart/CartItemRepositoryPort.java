package com.fhcs.clothing_store.application.port.out.cart;

import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;

public interface CartItemRepositoryPort {
    CartItemBO save(CartItemBO item);
    Optional<CartItemBO> findById(Integer cartItemId);
    Optional<CartItemBO> findByCartIdAndVariationId(Integer cartId, Integer variationId);
    void delete(CartItemBO item);
}
