package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {
    
}
