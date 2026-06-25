package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.address.AddressRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address.AddressRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.AddressEntityMapper;

@Component
public class AddressRepositoryAdapter implements AddressRepositoryPort {

    private final AddressRepository addressRepository;
    private final AddressEntityMapper mapper;

    public AddressRepositoryAdapter(AddressRepository addressRepository, AddressEntityMapper mapper) {
        this.addressRepository = addressRepository;
        this.mapper = mapper;
    }

    @Override
    public AddressBO save(AddressBO bo) {
        return mapper.addressToBO(addressRepository.save(mapper.addressToEntity(bo)));
    }

    @Override
    public List<IndividualAddressBO> findIndividualAddressesByIndividualId(Integer individualId) {
        return addressRepository.findIndividualAddressesByIndividualId(individualId).stream()
                .map(mapper::individualAddressToBO)
                .collect(Collectors.toList());
    }
}
