package com.fhcs.clothing_store.core.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViaCepDataBO {
    private String cep;
    private String street;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String ibgeCode;
    private Boolean error;
}
