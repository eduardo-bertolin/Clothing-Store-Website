package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.variation.SizeRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.variation.SizeBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation.SizeRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class SizeRepositoryAdapter implements SizeRepositoryPort {

    private final SizeRepository sizeRepository;
    private final ProductEntityMapper mapper;

    public SizeRepositoryAdapter(SizeRepository sizeRepository, ProductEntityMapper mapper) {
        this.sizeRepository = sizeRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SizeBO> findById(Integer id) {
        return sizeRepository.findById(id).map(mapper::sizeToBO);
    }
}
