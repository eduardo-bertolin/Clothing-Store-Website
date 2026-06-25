package com.fhcs.clothing_store.application.port.in.service;

import java.util.List;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.AddressPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.PrivateIndividualPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.AddressRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.PrivateIndividualRequest;

public interface PrivateIndividualServicePort {
    PrivateIndividualBO createPrivateIndividual(String accessToken, PrivateIndividualRequest request);
    PrivateIndividualBO getPrivateIndividualByToken(String accessToken);
    PrivateIndividualBO updatePrivateIndividual(String accessToken, PrivateIndividualPatchDto patch);
    void deletePrivateIndividual(String accessToken);
    List<IndividualAddressBO> addAddress(String accessToken, AddressRequest request);
    List<IndividualAddressBO> getAddresses(String accessToken);
    List<IndividualAddressBO> updateAddress(String accessToken, AddressPatchDto patch, Integer addressId);
    List<IndividualAddressBO> deleteAddress(String accessToken, Integer addressId);
}
