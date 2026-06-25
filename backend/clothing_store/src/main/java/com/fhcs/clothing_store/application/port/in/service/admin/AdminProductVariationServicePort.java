package com.fhcs.clothing_store.application.port.in.service.admin;

import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.ProductVariationPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.variation.ProductVariationRequest;

public interface AdminProductVariationServicePort {
    ProductVariationBO createProductVariation(Integer productId, ProductVariationRequest request);
    ProductVariationBO getProductVariationById(Integer variationId);
    ProductVariationBO updateProductVariation(Integer variationId, ProductVariationPatchDto patch);
    void deleteProductVariation(Integer variationId);
}
