package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.product.CategoryRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.CategoryRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryRepository categoryRepository;
    private final ProductEntityMapper mapper;

    public CategoryRepositoryAdapter(CategoryRepository categoryRepository,
            ProductEntityMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public CategoryBO save(CategoryBO bo) {
        return mapper.categoryToBO(categoryRepository.save(mapper.categoryToEntity(bo)));
    }

    @Override
    public Optional<CategoryBO> findById(Integer id) {
        return categoryRepository.findById(id).map(mapper::categoryToBO);
    }

    @Override
    public List<CategoryBO> findAll() {
        return categoryRepository.findAll().stream()
                .map(mapper::categoryToBO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CategoryBO bo) {
        categoryRepository.delete(mapper.categoryToEntity(bo));
    }
}
