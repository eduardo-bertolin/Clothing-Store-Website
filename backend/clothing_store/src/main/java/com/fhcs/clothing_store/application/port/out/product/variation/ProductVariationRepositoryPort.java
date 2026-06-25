package com.fhcs.clothing_store.application.port.out.product.variation;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;

public interface ProductVariationRepositoryPort {
    ProductVariationBO save(ProductVariationBO variation);
    Optional<ProductVariationBO> findById(Integer id);
    Page<ProductVariationBO> findFiltered(Integer categoryId, Integer collectionId, Integer colorId,
            Integer sizeId, Boolean inStock, Pageable pageable);
    void delete(ProductVariationBO variation);
}
