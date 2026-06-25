package com.fhcs.clothing_store.application.port.in.service.admin;

import java.util.List;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CollectionPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CollectionRequest;

public interface AdminCollectionServicePort {
    List<CollectionBO> createCollection(CollectionRequest request);
    List<CollectionBO> getAllCollections();
    CollectionBO getCollectionById(Integer collectionId);
    CollectionBO updateCollection(CollectionPatchDto patch, Integer collectionId);
    void deleteCollection(Integer collectionId);
}
