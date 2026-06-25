package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product,Integer> { 
  
} 
