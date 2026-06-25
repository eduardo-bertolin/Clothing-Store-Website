package com.fhcs.clothing_store.application.service.admin;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminProductVariationServicePort;
import com.fhcs.clothing_store.application.port.out.product.variation.ColorRepositoryPort;
import com.fhcs.clothing_store.application.port.out.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.application.port.out.product.variation.SizeRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ColorBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.SizeBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.ProductVariationPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.variation.ProductVariationRequest;

@Service
public class AdminProductVariationUseCase implements AdminProductVariationServicePort {

    private final ProductVariationRepositoryPort variationRepository;
    private final ColorRepositoryPort colorRepository;
    private final SizeRepositoryPort sizeRepository;
    private final AdminProductUseCase productUseCase;

    public AdminProductVariationUseCase(ProductVariationRepositoryPort variationRepository,
            ColorRepositoryPort colorRepository, SizeRepositoryPort sizeRepository,
            AdminProductUseCase productUseCase) {
        this.variationRepository = variationRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productUseCase = productUseCase;
    }

    @Override
    public ProductVariationBO createProductVariation(Integer productId,
            ProductVariationRequest request) {
        ProductBO product = productUseCase.getProductById(productId);
        ColorBO color = colorRepository.findById(request.getColorId()).orElseThrow();
        SizeBO size = sizeRepository.findById(request.getSizeId()).orElseThrow();

        ProductVariationBO variation = new ProductVariationBO();
        variation.setProduct(product);
        variation.setColor(color);
        variation.setSize(size);
        variation.setSkuCode(request.getSkuCode());
        variation.setStock(request.getStock());

        return variationRepository.save(variation);
    }

    @Override
    public ProductVariationBO getProductVariationById(Integer variationId) {
        return variationRepository.findById(variationId).orElseThrow();
    }

    @Override
    public ProductVariationBO updateProductVariation(Integer variationId,
            ProductVariationPatchDto patch) {
        ProductVariationBO variation = getProductVariationById(variationId);

        if (patch.getColorId() != null) {
            variation.setColor(colorRepository.findById(patch.getColorId()).orElseThrow());
        }
        if (patch.getSizeId() != null) {
            variation.setSize(sizeRepository.findById(patch.getSizeId()).orElseThrow());
        }
        if (patch.getSkuCode() != null) {
            variation.setSkuCode(patch.getSkuCode());
        }

        return variationRepository.save(variation);
    }

    @Override
    public void deleteProductVariation(Integer variationId) {
        ProductVariationBO variation = getProductVariationById(variationId);
        variationRepository.delete(variation);
    }
}
