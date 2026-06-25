package com.fhcs.clothing_store.application.port.out;

import com.fhcs.clothing_store.core.domain.bo.ViaCepDataBO;

public interface ViaCepPort {
    ViaCepDataBO fetchAddressByCep(String cep);
}
