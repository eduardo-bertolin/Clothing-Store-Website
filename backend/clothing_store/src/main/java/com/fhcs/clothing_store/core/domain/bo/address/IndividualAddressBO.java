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
public class IndividualAddressBO {
    private Integer individualAddressId;
    private AddressBO address;
    private String description;
}
