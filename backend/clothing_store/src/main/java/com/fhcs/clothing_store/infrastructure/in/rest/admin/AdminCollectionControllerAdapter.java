package com.fhcs.clothing_store.infrastructure.in.rest.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminCollectionServicePort;
import com.fhcs.clothing_store.core.domain.bo.product.CollectionBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.CollectionPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.CollectionRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.CollectionResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.ProductDtoMapper;
import com.fhcs.clothing_store.util.JsonPatchUtil;
import com.github.fge.jsonpatch.JsonPatch;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequestMapping("/api/admin/collections")
public class AdminCollectionControllerAdapter {

    private final AdminCollectionServicePort collectionService;
    private final JsonPatchUtil jsonPatchUtil;
    private final ProductDtoMapper productMapper;

    public AdminCollectionControllerAdapter(AdminCollectionServicePort collectionService,
            JsonPatchUtil jsonPatchUtil, ProductDtoMapper productMapper) {
        this.collectionService = collectionService;
        this.jsonPatchUtil = jsonPatchUtil;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> createCollection(
            @RequestBody CollectionRequest request) {
        try {
            List<CollectionBO> collections = collectionService.createCollection(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productMapper.toCollectionResponse(collections, "Coleção criada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CollectionResponse.error("Falha ao criar coleção: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<CollectionResponse> getAllCollections() {
        try {
            List<CollectionBO> collections = collectionService.getAllCollections();
            return ResponseEntity.ok(
                    productMapper.toCollectionResponse(collections, "Coleções recuperadas com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CollectionResponse.error("Falha ao recuperar coleções: " + e.getMessage()));
        }
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> getCollectionById(@PathVariable Integer collectionId) {
        try {
            CollectionBO collection = collectionService.getCollectionById(collectionId);
            return ResponseEntity.ok(productMapper.toCollectionResponse(List.of(collection),
                    "Coleção recuperada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CollectionResponse.error("Falha ao recuperar coleção: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> updateCollection(@RequestBody JsonPatch patch,
            @PathVariable Integer collectionId) {
        try {
            CollectionPatchDto patchDto =
                    jsonPatchUtil.extractPatchedFields(patch, CollectionPatchDto.class);
            CollectionBO collection = collectionService.updateCollection(patchDto, collectionId);
            return ResponseEntity.ok(productMapper.toCollectionResponse(List.of(collection),
                    "Coleção atualizada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CollectionResponse.error("Falha ao atualizar coleção: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> deleteCollection(
            @PathVariable Integer collectionId) {
        try {
            collectionService.deleteCollection(collectionId);
            List<CollectionBO> collections = collectionService.getAllCollections();
            if (collections.isEmpty()) {
                return ResponseEntity.ok(
                        CollectionResponse.messageOnly(true, "Coleção deletada com sucesso."));
            }
            return ResponseEntity.ok(
                    productMapper.toCollectionResponse(collections, "Coleção deletada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CollectionResponse.error("Falha ao deletar coleção: " + e.getMessage()));
        }
    }
}
