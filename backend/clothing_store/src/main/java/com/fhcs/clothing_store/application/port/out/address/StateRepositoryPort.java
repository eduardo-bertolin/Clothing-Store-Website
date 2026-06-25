package com.fhcs.clothing_store.application.port.out.address;

import com.fhcs.clothing_store.core.domain.bo.address.StateBO;

public interface StateRepositoryPort {
    StateBO findByUf(String uf);
}
