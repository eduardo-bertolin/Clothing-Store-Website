package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponse {

    private boolean success;
    private String message;
    private ProductBO product;

    public static ProductResponse success(ProductBO product, String message) {
        return ProductResponse.builder()
                .success(true)
                .message(message)
                .product(product)
                .build();
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
