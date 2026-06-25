package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.CEP;

public interface CEPRepository extends JpaRepository<CEP, Integer> {

    public CEP findByCepNumber(String cepNumber);
}
