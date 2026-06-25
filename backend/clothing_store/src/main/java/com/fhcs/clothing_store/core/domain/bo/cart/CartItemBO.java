package com.fhcs.clothing_store.core.domain.bo.cart;

import java.math.BigDecimal;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
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
public class CartItemBO {
    private Integer cartItemId;
    private Integer cartId;
    private ProductVariationBO variation;
    private Integer quantity;
    private BigDecimal unitPrice;
}
