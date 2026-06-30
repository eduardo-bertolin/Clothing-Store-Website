package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import java.time.Instant;

import com.fhcs.clothing_store.core.domain.bo.image.ImageType;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageDto {

    private Integer id;
    private String imageUrl;
    private ImageType type;
    private Integer position;
    private Integer variationId;
    private Instant createdAt;

    public static ProductImageDto from(ProductImageBO bo) {
        return ProductImageDto.builder()
                .id(bo.getId())
                .imageUrl(bo.getImageUrl())
                .type(bo.getType())
                .position(bo.getPosition())
                .variationId(bo.getVariation() != null ? bo.getVariation().getVariationId() : null)
                .createdAt(bo.getCreatedAt())
                .build();
    }
}
