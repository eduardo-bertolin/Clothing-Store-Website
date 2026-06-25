package com.fhcs.clothing_store.application.port.out.address;

import com.fhcs.clothing_store.core.domain.bo.address.CityBO;

public interface CityRepositoryPort {
    CityBO findByCityName(String cityName);
}
