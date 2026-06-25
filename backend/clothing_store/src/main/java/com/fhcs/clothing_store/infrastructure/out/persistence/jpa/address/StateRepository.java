package com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.State;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Integer> {
    
    public State findByStateName(String stateName);

    public State findByUf(String uf);
}
