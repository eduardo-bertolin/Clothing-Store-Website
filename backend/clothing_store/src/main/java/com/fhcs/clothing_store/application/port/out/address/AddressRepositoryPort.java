package com.fhcs.clothing_store.application.port.out.address;

import java.util.List;
import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;

public interface AddressRepositoryPort {
    AddressBO save(AddressBO address);
    List<IndividualAddressBO> findIndividualAddressesByIndividualId(Integer individualId);
}
