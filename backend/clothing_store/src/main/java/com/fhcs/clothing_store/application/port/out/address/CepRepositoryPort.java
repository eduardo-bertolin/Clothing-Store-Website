package com.fhcs.clothing_store.application.port.out.address;

import com.fhcs.clothing_store.core.domain.bo.address.CepBO;

public interface CepRepositoryPort {
    CepBO save(CepBO cep);
    CepBO findByCepNumber(String cepNumber);
}
