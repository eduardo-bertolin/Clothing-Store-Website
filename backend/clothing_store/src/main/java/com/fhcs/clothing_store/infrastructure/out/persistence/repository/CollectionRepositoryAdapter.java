package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.CollectionRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.CollectionRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class CollectionRepositoryAdapter implements CollectionRepositoryPort {

    private final CollectionRepository collectionRepository;
    private final ProductEntityMapper mapper;

    public CollectionRepositoryAdapter(CollectionRepository collectionRepository,
            ProductEntityMapper mapper) {
        this.collectionRepository = collectionRepository;
        this.mapper = mapper;
    }

    @Override
    public CollectionBO save(CollectionBO bo) {
        return mapper.collectionToBO(collectionRepository.save(mapper.collectionToEntity(bo)));
    }

    @Override
    public Optional<CollectionBO> findById(Integer id) {
        return collectionRepository.findById(id).map(mapper::collectionToBO);
    }

    @Override
    public List<CollectionBO> findAll() {
        return collectionRepository.findAll().stream()
                .map(mapper::collectionToBO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CollectionBO bo) {
        collectionRepository.delete(mapper.collectionToEntity(bo));
    }
}
