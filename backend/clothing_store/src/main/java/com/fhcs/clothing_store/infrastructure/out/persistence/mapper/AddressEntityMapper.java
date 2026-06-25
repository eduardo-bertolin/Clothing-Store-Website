package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.CepBO;
import com.fhcs.clothing_store.core.domain.bo.address.CityBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.StateBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.Address;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.CEP;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.City;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.IndividualAddress;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.State;

@Component
public class AddressEntityMapper {

    public CepBO cepToBO(CEP entity) {
        if (entity == null) return null;
        CepBO bo = new CepBO();
        bo.setCepId(entity.getCepId());
        bo.setCepNumber(entity.getCepNumber());
        return bo;
    }

    public CEP cepToEntity(CepBO bo) {
        if (bo == null) return null;
        CEP entity = new CEP();
        entity.setCepId(bo.getCepId());
        entity.setCepNumber(bo.getCepNumber());
        return entity;
    }

    public CityBO cityToBO(City entity) {
        if (entity == null) return null;
        CityBO bo = new CityBO();
        bo.setCityId(entity.getCityId());
        bo.setCityName(entity.getCityName());
        return bo;
    }

    public City cityToEntity(CityBO bo) {
        if (bo == null) return null;
        City entity = new City();
        entity.setCityId(bo.getCityId());
        entity.setCityName(bo.getCityName());
        return entity;
    }

    public StateBO stateToBO(State entity) {
        if (entity == null) return null;
        StateBO bo = new StateBO();
        bo.setStateId(entity.getStateId());
        bo.setStateName(entity.getStateName());
        bo.setUf(entity.getUf());
        return bo;
    }

    public State stateToEntity(StateBO bo) {
        if (bo == null) return null;
        State entity = new State();
        entity.setStateId(bo.getStateId());
        entity.setStateName(bo.getStateName());
        entity.setUf(bo.getUf());
        return entity;
    }

    public AddressBO addressToBO(Address entity) {
        if (entity == null) return null;
        return AddressBO.builder()
                .addressId(entity.getAddressId())
                .cep(cepToBO(entity.getCep()))
                .city(cityToBO(entity.getCity()))
                .state(stateToBO(entity.getState()))
                .streetName(entity.getStreetName())
                .number(entity.getNumber())
                .complement(entity.getComplement())
                .build();
    }

    public Address addressToEntity(AddressBO bo) {
        if (bo == null) return null;
        return Address.builder()
                .addressId(bo.getAddressId())
                .cep(cepToEntity(bo.getCep()))
                .city(cityToEntity(bo.getCity()))
                .state(stateToEntity(bo.getState()))
                .streetName(bo.getStreetName())
                .number(bo.getNumber())
                .complement(bo.getComplement())
                .build();
    }

    public IndividualAddressBO individualAddressToBO(IndividualAddress entity) {
        if (entity == null) return null;
        return IndividualAddressBO.builder()
                .individualAddressId(entity.getIndividualAddressId())
                .address(addressToBO(entity.getAddress()))
                .description(entity.getDescription())
                .build();
    }

    public IndividualAddress individualAddressToEntity(IndividualAddressBO bo) {
        if (bo == null) return null;
        return IndividualAddress.builder()
                .individualAddressId(bo.getIndividualAddressId())
                .address(addressToEntity(bo.getAddress()))
                .description(bo.getDescription())
                .build();
    }
}
