package com.fhcs.clothing_store.application.port.in.service;

import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageRequestBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageUpdateRequestBO;

public interface ImageServicePort {
    List<ProductImageBO> uploadProductImages(ProductImageRequestBO bo);
    List<ProductImageBO> updateProductImages(ProductImageUpdateRequestBO bo);
}
