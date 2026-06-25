package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
}
