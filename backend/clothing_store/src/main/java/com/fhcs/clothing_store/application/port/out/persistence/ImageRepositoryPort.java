package com.fhcs.clothing_store.application.port.out.persistence;

import java.util.List;
import java.util.Optional;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;

public interface ImageRepositoryPort {
    List<ProductImageBO> saveAll(List<ProductImageBO> images);
    ProductImageBO save(ProductImageBO image);
    List<ProductImageBO> findByProductId(Integer productId);
    List<ProductImageBO> findByProductIds(List<Integer> productIds);
    Optional<ProductImageBO> findById(Integer id);
    void deleteById(Integer id);
}
