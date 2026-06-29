package com.fhcs.clothing_store.core.domain.bo.image;

import java.time.Instant;

import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;

public class ProductImageBO {
    private Integer id;
    private ProductBO product;
    private ProductVariationBO variation;
    private String imageUrl;
    private ImageType type;
    private Integer position;
    private Instant createdAt;

    private ProductImageBO() {
    }

    public static ProductImageBO DRAFT(ProductBO product, String url, ImageType type, Integer posistion) {
        ProductImageBO imageBO = new ProductImageBO();
        imageBO.setProductBO(product);
        imageBO.setImageUrl(url);
        imageBO.setType(type);
        imageBO.setPosition(posistion);
        imageBO.setCreatedAt(Instant.now());

        return imageBO;
    }

    public static ProductImageBO FULFILLED(Integer id, ProductBO product, String url, ImageType type,
            Integer posistion, Instant createdAt) {
        ProductImageBO imageBO = new ProductImageBO();
        imageBO.setId(id);
        imageBO.setProductBO(product);
        imageBO.setImageUrl(url);
        imageBO.setType(type);
        imageBO.setPosition(posistion);
        imageBO.setCreatedAt(createdAt);

        return imageBO;
    }

    public void assignToVariation(ProductVariationBO variationBO) {
        setVariationBO(variationBO);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductBO getProductBO() {
        return product;
    }

    public void setProductBO(ProductBO product) {
        this.product = product;
    }

    public ProductVariationBO getVariationBO() {
        return variation;
    }

    public void setVariationBO(ProductVariationBO variationId) {
        this.variation = variationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
