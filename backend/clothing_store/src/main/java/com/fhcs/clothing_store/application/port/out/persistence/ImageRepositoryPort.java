package com.fhcs.clothing_store.application.port.out.persistence;

import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;

public interface ImageRepositoryPort {
    List<ProductImageBO> saveAll(List<ProductImageBO> images);
    List<ProductImageBO> findByProductId(Integer productId);
    List<ProductImageBO> findByProductIds(List<Integer> productIds);
    void deleteById(Integer id);
}
