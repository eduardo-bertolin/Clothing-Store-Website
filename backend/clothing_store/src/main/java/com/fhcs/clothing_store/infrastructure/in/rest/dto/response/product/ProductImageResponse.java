package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductImageResponse {

    private boolean success;
    private String message;
    private List<ProductImageDto> productImages;

    public static ProductImageResponse success(String message, List<ProductImageBO> productImages) {
        List<ProductImageDto> dtos = productImages.stream()
                .map(ProductImageDto::from)
                .toList();
        return ProductImageResponse.builder()
            .success(true)
            .message(message)
            .productImages(dtos)
            .build();
    }

    public static ProductImageResponse error(String message) {
        return ProductImageResponse.builder()
            .success(false)
            .message(message)
            .build();
    }
    
}
