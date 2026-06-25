package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.Size;

public interface SizeRepository extends JpaRepository<Size,Integer> {
    
}
