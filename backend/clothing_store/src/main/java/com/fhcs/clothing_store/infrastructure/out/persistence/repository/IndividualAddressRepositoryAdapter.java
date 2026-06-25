package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.address.IndividualAddressRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address.IndividualAddressRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.AddressEntityMapper;

@Component
public class IndividualAddressRepositoryAdapter implements IndividualAddressRepositoryPort {

    private final IndividualAddressRepository repository;
    private final AddressEntityMapper mapper;

    public IndividualAddressRepositoryAdapter(IndividualAddressRepository repository,
            AddressEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public IndividualAddressBO save(IndividualAddressBO bo) {
        return mapper.individualAddressToBO(
                repository.save(mapper.individualAddressToEntity(bo)));
    }

    @Override
    public void delete(IndividualAddressBO bo) {
        repository.delete(mapper.individualAddressToEntity(bo));
    }
}
