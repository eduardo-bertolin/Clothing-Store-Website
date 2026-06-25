package com.fhcs.clothing_store.application.service.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminCollectionServicePort;
import com.fhcs.clothing_store.application.port.out.product.CollectionRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CollectionPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CollectionRequest;

@Service
public class AdminCollectionUseCase implements AdminCollectionServicePort {

    private final CollectionRepositoryPort collectionRepository;

    public AdminCollectionUseCase(CollectionRepositoryPort collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    @Override
    public List<CollectionBO> createCollection(CollectionRequest request) {
        CollectionBO collection = new CollectionBO();
        collection.setCollectionName(request.getCollectionName());
        collection.setDescription(request.getDescription());
        collection.setLaunchDate(
                LocalDate.parse(request.getLaunchDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        collectionRepository.save(collection);
        return collectionRepository.findAll();
    }

    @Override
    public List<CollectionBO> getAllCollections() {
        return collectionRepository.findAll();
    }

    @Override
    public CollectionBO getCollectionById(Integer collectionId) {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException(
                        "Coleção não encontrada com ID: " + collectionId));
    }

    @Override
    public CollectionBO updateCollection(CollectionPatchDto patch, Integer collectionId) {
        CollectionBO collection = getCollectionById(collectionId);
        if (patch.getCollectionName() != null) {
            collection.setCollectionName(patch.getCollectionName());
        }
        if (patch.getDescription() != null) {
            collection.setDescription(patch.getDescription());
        }
        if (patch.getLaunchDate() != null) {
            collection.setLaunchDate(LocalDate.parse(patch.getLaunchDate(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        return collectionRepository.save(collection);
    }

    @Override
    public void deleteCollection(Integer collectionId) {
        CollectionBO collection = getCollectionById(collectionId);
        collectionRepository.delete(collection);
    }
}
