package com.fhcs.clothing_store.infrastructure.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.PrivateIndividual;

public interface PrivateIndividualRepository extends JpaRepository<PrivateIndividual, Integer> {
    
    PrivateIndividual findByUser_UserId(Integer userId);

    boolean existsByPhoneNumberAndIndividualIdNot(String formattedPhoneNumber, Integer individualId);
}
