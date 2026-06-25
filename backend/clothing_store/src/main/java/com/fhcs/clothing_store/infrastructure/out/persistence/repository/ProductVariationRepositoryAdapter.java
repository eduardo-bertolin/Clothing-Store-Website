package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.ProductVariation;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation.ProductVariationRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class ProductVariationRepositoryAdapter implements ProductVariationRepositoryPort {

    private final ProductVariationRepository variationRepository;
    private final ProductEntityMapper mapper;

    public ProductVariationRepositoryAdapter(ProductVariationRepository variationRepository,
            ProductEntityMapper mapper) {
        this.variationRepository = variationRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductVariationBO save(ProductVariationBO bo) {
        return mapper.variationToBO(variationRepository.save(mapper.variationToEntity(bo)));
    }

    @Override
    public Optional<ProductVariationBO> findById(Integer id) {
        return variationRepository.findById(id).map(mapper::variationToBO);
    }

    @Override
    public void delete(ProductVariationBO bo) {
        variationRepository.delete(mapper.variationToEntity(bo));
    }

    @Override
    public Page<ProductVariationBO> findFiltered(Integer categoryId, Integer collectionId,
            Integer colorId, Integer sizeId, Boolean inStock, Pageable pageable) {

        Specification<ProductVariation> spec = Specification.where(null);

        if (categoryId != null) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("product").get("category").get("categoryId"), categoryId));
        }
        if (collectionId != null) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("product").get("collection").get("collectionId"),
                            collectionId));
        }
        if (colorId != null) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("color").get("colorId"), colorId));
        }
        if (sizeId != null) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("size").get("sizeId"), sizeId));
        }
        if (Boolean.TRUE.equals(inStock)) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThan(root.get("stock"), 0));
        }

        return variationRepository.findAll(spec, pageable).map(mapper::variationToBO);
    }
}
