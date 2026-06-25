package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import java.util.List;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryResponse {

    private boolean success;
    private String message;
    private List<Category> categories;

    public static CategoryResponse success(List<Category> categories, String message) {
        return CategoryResponse.builder()
                .success(true)
                .message(message)
                .categories(categories)
                .build();
    }

    public static CategoryResponse error(String message) {
        return CategoryResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static CategoryResponse messageOnly(boolean success, String message) {
        return CategoryResponse.builder()
                .success(success)
                .message(message)
                .build();
    }

    public static CategoryResponse successBO(
            List<com.fhcs.clothing_store.core.domain.bo.product.CategoryBO> bos, String message) {
        List<Category> cats = bos.stream().map(bo -> {
            Category c = new Category();
            c.setCategoryId(bo.getCategoryId());
            c.setCategoryName(bo.getCategoryName());
            return c;
        }).toList();
        return CategoryResponse.builder().success(true).message(message).categories(cats).build();
    }
}
