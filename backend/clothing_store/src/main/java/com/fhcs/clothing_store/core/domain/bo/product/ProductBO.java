package com.fhcs.clothing_store.core.domain.bo.product;

import java.math.BigDecimal;
import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBO {
    private Integer productId;
    private CollectionBO collection;
    private CategoryBO category;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer score;
    private List<ProductImageBO> images;
}
