package com.fhcs.clothing_store.core.domain.bo.order;

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
public class OrderItemBO {
    private Integer orderItemId;
    private ProductVariationBO variation;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
