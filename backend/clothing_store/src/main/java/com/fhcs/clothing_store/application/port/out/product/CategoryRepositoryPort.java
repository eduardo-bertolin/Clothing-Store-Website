package com.fhcs.clothing_store.application.port.out.product;

import java.util.List;
import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;

public interface CategoryRepositoryPort {
    CategoryBO save(CategoryBO category);
    Optional<CategoryBO> findById(Integer id);
    List<CategoryBO> findAll();
    void delete(CategoryBO category);
}
