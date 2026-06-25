package com.fhcs.clothing_store.application.port.out.product;

import java.util.List;
import java.util.Optional;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;

public interface CollectionRepositoryPort {
    CollectionBO save(CollectionBO collection);
    Optional<CollectionBO> findById(Integer id);
    List<CollectionBO> findAll();
    void delete(CollectionBO collection);
}
