package com.fhcs.clothing_store.application.port.in.service.admin;

import java.util.List;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CategoryPatch;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CategoryRequest;

public interface AdminCategoryServicePort {
    List<CategoryBO> createCategory(CategoryRequest request);
    List<CategoryBO> getAllCategories();
    CategoryBO getCategoryById(Integer id);
    CategoryBO updateCategory(CategoryPatch patch, Integer categoryId);
    void deleteCategoryById(Integer categoryId);
}
