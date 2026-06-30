package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.ProductImage;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Product;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.ProductVariation;

import jakarta.persistence.EntityManager;

@Component
public class ProductImageEntityMapper {

    private final ProductEntityMapper productEntityMapper;
    private final EntityManager entityManager;

    public ProductImageEntityMapper(ProductEntityMapper productEntityMapper, EntityManager entityManager) {
        this.productEntityMapper = productEntityMapper;
        this.entityManager = entityManager;
    }

    public ProductImageBO toBO(ProductImage entity) {
        if (entity == null)
            return null;

        ProductImageBO productImage = ProductImageBO.FULFILLED(
                entity.getId(),
                productEntityMapper.productToBO(entity.getProduct()),
                entity.getImageUrl(),
                entity.getType(),
                entity.getPosition(),
                entity.getCreatedAt());

        if (entity.getVariation() != null) productImage.assignToVariation(productEntityMapper.variationToBO(entity.getVariation()));

        return productImage;
    }

    public ProductImage toEntity(ProductImageBO bo) {
        if (bo == null)
            return null;
        ProductImage productImage = new ProductImage();
        productImage.setId(bo.getId());
        productImage.setProduct(entityManager.getReference(Product.class, bo.getProduct().getProductId()));
        if (bo.getVariation() != null) {
            productImage.setVariation(entityManager.getReference(ProductVariation.class, bo.getVariation().getVariationId()));
        }
        productImage.setImageUrl(bo.getImageUrl());
        productImage.setType(bo.getType());
        productImage.setPosition(bo.getPosition());
        productImage.setCreatedAt(bo.getCreatedAt());

        return productImage;
    }
}
