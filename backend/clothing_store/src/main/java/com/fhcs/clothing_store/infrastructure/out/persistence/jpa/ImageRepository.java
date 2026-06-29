package com.fhcs.clothing_store.infrastructure.out.persistence.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.ProductImage;

@Repository
public interface ImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductProductId(Integer productId);
    List<ProductImage> findByProductProductIdIn(List<Integer> productIds);
}
