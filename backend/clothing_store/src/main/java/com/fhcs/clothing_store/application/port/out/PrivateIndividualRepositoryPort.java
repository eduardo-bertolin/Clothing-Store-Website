package com.fhcs.clothing_store.application.port.out;

import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;

public interface PrivateIndividualRepositoryPort {
    PrivateIndividualBO save(PrivateIndividualBO individual);
    PrivateIndividualBO findByUserId(Integer userId);
    void delete(PrivateIndividualBO individual);
    boolean existsByPhoneNumberAndIndividualIdNot(String phoneNumber, Integer individualId);
}
