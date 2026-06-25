package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.IndividualAddress;

public interface IndividualAddressRepository extends JpaRepository<IndividualAddress, Integer> {
    
}
