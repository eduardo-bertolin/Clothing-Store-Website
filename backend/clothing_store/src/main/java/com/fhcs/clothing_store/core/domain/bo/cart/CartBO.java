package com.fhcs.clothing_store.core.domain.bo.cart;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartBO {
    private Integer cartId;
    private Integer individualId;
    @Builder.Default
    private CartStatusBO status = CartStatusBO.ACTIVE;
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();
    @Builder.Default
    private List<CartItemBO> items = new ArrayList<>();
}
