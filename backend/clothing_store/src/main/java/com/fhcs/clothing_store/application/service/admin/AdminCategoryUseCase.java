package com.fhcs.clothing_store.application.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminCategoryServicePort;
import com.fhcs.clothing_store.application.port.out.product.CategoryRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CategoryPatch;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CategoryRequest;

@Service
public class AdminCategoryUseCase implements AdminCategoryServicePort {

    private final CategoryRepositoryPort categoryRepository;

    public AdminCategoryUseCase(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryBO> createCategory(CategoryRequest request) {
        CategoryBO category = new CategoryBO();
        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
        return categoryRepository.findAll();
    }

    @Override
    public List<CategoryBO> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public CategoryBO getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria nao encontrada com id: " + id));
    }

    @Override
    public CategoryBO updateCategory(CategoryPatch patch, Integer categoryId) {
        CategoryBO category = getCategoryById(categoryId);
        if (patch.getCategoryName() != null) {
            category.setCategoryName(patch.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Integer categoryId) {
        CategoryBO category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }
}
