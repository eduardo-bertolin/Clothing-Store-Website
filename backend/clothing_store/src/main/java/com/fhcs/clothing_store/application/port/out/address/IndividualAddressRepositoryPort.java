package com.fhcs.clothing_store.application.port.out.address;

import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;

public interface IndividualAddressRepositoryPort {
    IndividualAddressBO save(IndividualAddressBO individualAddress);
    void delete(IndividualAddressBO individualAddress);
}
