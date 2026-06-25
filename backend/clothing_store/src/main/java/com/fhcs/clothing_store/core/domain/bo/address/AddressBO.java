package com.fhcs.clothing_store.core.domain.bo.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressBO {
    private Integer addressId;
    private CepBO cep;
    private CityBO city;
    private StateBO state;
    private String streetName;
    private Integer number;
    private String complement;
}
