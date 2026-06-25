package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.address.CityRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.address.CityBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.address.CityRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.AddressEntityMapper;

@Component
public class CityRepositoryAdapter implements CityRepositoryPort {

    private final CityRepository cityRepository;
    private final AddressEntityMapper mapper;

    public CityRepositoryAdapter(CityRepository cityRepository, AddressEntityMapper mapper) {
        this.cityRepository = cityRepository;
        this.mapper = mapper;
    }

    @Override
    public CityBO findByCityName(String cityName) {
        return mapper.cityToBO(cityRepository.findByCityName(cityName));
    }
}
