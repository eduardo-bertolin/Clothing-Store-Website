package com.fhcs.clothing_store.infrastructure.in.rest.dto.response;

import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.address.Address;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AddressDto {
    private Integer id;
    private String street;
    private Integer number;
    private String city;
    private String state;
    private String cep;
    private String complement;
    private String description;

    public static AddressDto fromAddress(Address address, String description) {
        String cep = address.getCep().getCepNumber();
        String formattedCep = cep.replaceAll("(\\d{5})", "$1-");
        String complement = address.getComplement() != null ? address.getComplement() : "";
        return AddressDto.builder()
                .id(address.getAddressId())
                .street(address.getStreetName())
                .number(address.getNumber())
                .city(address.getCity().getCityName())
                .state(address.getState().getStateName())
                .cep(formattedCep)
                .complement(complement)
                .description(description)
                .build();
    }

    public static AddressDto fromBO(IndividualAddressBO ia) {
        AddressBO address = ia.getAddress();
        String cep = address.getCep().getCepNumber();
        String formattedCep = cep.replaceAll("(\\d{5})", "$1-");
        String complement = address.getComplement() != null ? address.getComplement() : "";
        return AddressDto.builder()
                .id(address.getAddressId())
                .street(address.getStreetName())
                .number(address.getNumber())
                .city(address.getCity().getCityName())
                .state(address.getState().getStateName())
                .cep(formattedCep)
                .complement(complement)
                .description(ia.getDescription())
                .build();
    }
}
