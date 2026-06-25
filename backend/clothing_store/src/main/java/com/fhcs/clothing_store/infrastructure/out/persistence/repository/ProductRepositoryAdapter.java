package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.ProductRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.ProductRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryAdapter(ProductRepository productRepository,
            ProductEntityMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductBO save(ProductBO bo) {
        return mapper.productToBO(productRepository.save(mapper.productToEntity(bo)));
    }

    @Override
    public Optional<ProductBO> findById(Integer id) {
        return productRepository.findById(id).map(mapper::productToBO);
    }

    @Override
    public Page<ProductBO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(mapper::productToBO);
    }

    @Override
    public void delete(ProductBO bo) {
        productRepository.delete(mapper.productToEntity(bo));
    }
}
