package com.fhcs.clothing_store.infrastructure.in.rest.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminCategoryServicePort;
import com.fhcs.clothing_store.core.domain.bo.product.CategoryBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CategoryPatch;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CategoryRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.CategoryResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.ProductDtoMapper;
import com.fhcs.clothing_store.util.JsonPatchUtil;
import com.github.fge.jsonpatch.JsonPatch;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequestMapping("/api/admin/categories")
public class AdminCategoryControllerAdapter {

    private final AdminCategoryServicePort categoryService;
    private final JsonPatchUtil jsonPatchUtil;
    private final ProductDtoMapper productMapper;

    public AdminCategoryControllerAdapter(AdminCategoryServicePort categoryService,
            JsonPatchUtil jsonPatchUtil, ProductDtoMapper productMapper) {
        this.categoryService = categoryService;
        this.jsonPatchUtil = jsonPatchUtil;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        try {
            List<CategoryBO> categories = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productMapper.toCategoryResponse(categories, "Categoria criada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CategoryResponse.error("Erro ao criar categoria: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<CategoryResponse> getAllCategories() {
        try {
            List<CategoryBO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(
                    productMapper.toCategoryResponse(categories, "Categorias recuperadas com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CategoryResponse.error("Erro ao recuperar categorias: " + e.getMessage()));
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryBO category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(productMapper.toCategoryResponse(List.of(category),
                    "Categoria recuperada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CategoryResponse.error("Erro ao recuperar categoria: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody JsonPatch patch,
            @PathVariable Integer categoryId) {
        try {
            CategoryPatch patchDto = jsonPatchUtil.extractPatchedFields(patch, CategoryPatch.class);
            CategoryBO category = categoryService.updateCategory(patchDto, categoryId);
            return ResponseEntity.ok(productMapper.toCategoryResponse(List.of(category),
                    "Categoria atualizada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CategoryResponse.error("Erro ao atualizar categoria: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Integer categoryId) {
        try {
            categoryService.deleteCategoryById(categoryId);
            List<CategoryBO> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                return ResponseEntity.ok(
                        CategoryResponse.messageOnly(true, "Categoria deletada com sucesso"));
            }
            return ResponseEntity.ok(
                    productMapper.toCategoryResponse(categories, "Categoria deletada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CategoryResponse.error("Erro ao deletar categoria: " + e.getMessage()));
        }
    }
}
