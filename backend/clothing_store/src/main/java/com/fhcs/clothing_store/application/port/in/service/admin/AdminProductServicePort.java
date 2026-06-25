package com.fhcs.clothing_store.application.port.in.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.ProductPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.ProductRequest;

public interface AdminProductServicePort {
    ProductBO createProduct(ProductRequest request);
    ProductBO getProductById(Integer productId);
    Page<ProductBO> getAllProducts(Pageable pageable);
    ProductBO updateProduct(Integer productId, ProductPatchDto patch);
    void deleteProduct(Integer productId);
}
