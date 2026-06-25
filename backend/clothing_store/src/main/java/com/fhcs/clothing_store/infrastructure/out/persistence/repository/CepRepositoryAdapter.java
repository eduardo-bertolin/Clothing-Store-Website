package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.address.CepRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.address.CepBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address.CEPRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.AddressEntityMapper;

@Component
public class CepRepositoryAdapter implements CepRepositoryPort {

    private final CEPRepository cepRepository;
    private final AddressEntityMapper mapper;

    public CepRepositoryAdapter(CEPRepository cepRepository, AddressEntityMapper mapper) {
        this.cepRepository = cepRepository;
        this.mapper = mapper;
    }

    @Override
    public CepBO save(CepBO bo) {
        return mapper.cepToBO(cepRepository.save(mapper.cepToEntity(bo)));
    }

    @Override
    public CepBO findByCepNumber(String cepNumber) {
        return mapper.cepToBO(cepRepository.findByCepNumber(cepNumber));
    }
}
