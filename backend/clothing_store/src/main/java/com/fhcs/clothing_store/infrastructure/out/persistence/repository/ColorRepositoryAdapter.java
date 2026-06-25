package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.variation.ColorRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ColorBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation.ColorRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class ColorRepositoryAdapter implements ColorRepositoryPort {

    private final ColorRepository colorRepository;
    private final ProductEntityMapper mapper;

    public ColorRepositoryAdapter(ColorRepository colorRepository, ProductEntityMapper mapper) {
        this.colorRepository = colorRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ColorBO> findById(Integer id) {
        return colorRepository.findById(id).map(mapper::colorToBO);
    }
}
