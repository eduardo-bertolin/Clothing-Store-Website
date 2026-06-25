package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {
    
    private boolean success;
    private String message;
    private Product product;

    public static ProductResponse success(Product product, String message) {
        return ProductResponse.builder()
                .success(true)
                .message(message)
                .product(product)
                .build();
    }

    public static ProductResponse successBO(com.fhcs.clothing_store.core.domain.bo.product.ProductBO bo, String message) {
        Product p = new Product();
        p.setProductId(bo.getProductId());
        p.setName(bo.getName());
        p.setDescription(bo.getDescription());
        p.setPrice(bo.getPrice());
        p.setScore(bo.getScore());
        return ProductResponse.builder().success(true).message(message).product(p).build();
    }

    public static ProductResponse error(String message) {
        return ProductResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static ProductResponse messageOnly(boolean success, String message) {
        return ProductResponse.builder()
                .success(success)
                .message(message)
                .build();
    }
}
