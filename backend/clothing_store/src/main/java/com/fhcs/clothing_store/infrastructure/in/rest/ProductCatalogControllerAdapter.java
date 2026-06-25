package com.fhcs.clothing_store.infrastructure.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.out.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.variation.ProductVariationDto;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.ProductDtoMapper;

@RestController
@RequestMapping("/api/catalog")
public class ProductCatalogControllerAdapter {

    private final ProductVariationRepositoryPort variationRepository;
    private final ProductDtoMapper productMapper;

    public ProductCatalogControllerAdapter(ProductVariationRepositoryPort variationRepository,
            ProductDtoMapper productMapper) {
        this.variationRepository = variationRepository;
        this.productMapper = productMapper;
    }

    @GetMapping("/variations")
    public PagedModel<ProductVariationDto> getVariations(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer collectionId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) Integer sizeId,
            @RequestParam(required = false) Boolean inStock,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProductVariationBO> page = variationRepository.findFiltered(categoryId, collectionId,
                colorId, sizeId, inStock, pageable);
        return new PagedModel<>(page.map(productMapper::toProductVariationDto));
    }

    @GetMapping("/variations/{variationId}")
    public ProductVariationDto getVariationById(@PathVariable Integer variationId) {
        return variationRepository.findById(variationId)
                .map(productMapper::toProductVariationDto)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Variação não encontrada: " + variationId));
    }
}
