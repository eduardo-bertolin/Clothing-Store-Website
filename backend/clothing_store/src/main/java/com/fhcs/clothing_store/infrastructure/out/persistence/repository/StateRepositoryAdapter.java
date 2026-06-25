package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.address.StateRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.address.StateBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address.StateRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.AddressEntityMapper;

@Component
public class StateRepositoryAdapter implements StateRepositoryPort {

    private final StateRepository stateRepository;
    private final AddressEntityMapper mapper;

    public StateRepositoryAdapter(StateRepository stateRepository, AddressEntityMapper mapper) {
        this.stateRepository = stateRepository;
        this.mapper = mapper;
    }

    @Override
    public StateBO findByUf(String uf) {
        return mapper.stateToBO(stateRepository.findByUf(uf));
    }
}
