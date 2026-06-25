package com.fhcs.clothing_store.core.domain.bo.product.variation;

import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationBO {
    private Integer variationId;
    private ProductBO product;
    private ColorBO color;
    private SizeBO size;
    private String skuCode;
    private Integer stock;

    public boolean hasStock(int requested) {
        return this.stock >= requested;
    }
}
