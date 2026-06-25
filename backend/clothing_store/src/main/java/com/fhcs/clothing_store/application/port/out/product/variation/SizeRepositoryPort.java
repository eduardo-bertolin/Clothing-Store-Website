package com.fhcs.clothing_store.application.port.out.product.variation;

import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.product.variation.SizeBO;

public interface SizeRepositoryPort {
    Optional<SizeBO> findById(Integer id);
}
