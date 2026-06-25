package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product;

import java.util.List;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.Collection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CollectionResponse {

    private boolean success;
    private String message;
    private List<Collection> collections;

    public static CollectionResponse success(List<Collection> collections, String message) {
        return CollectionResponse.builder()
                .success(true)
                .message(message)
                .collections(collections)
                .build();
    }

    public static CollectionResponse successBO(
            List<com.fhcs.clothing_store.core.domain.bo.product.CollectionBO> bos, String message) {
        List<Collection> colls = bos.stream().map(bo -> {
            Collection c = new Collection();
            c.setCollectionId(bo.getCollectionId());
            c.setCollectionName(bo.getCollectionName());
            c.setDescription(bo.getDescription());
            c.setLaunchDate(bo.getLaunchDate());
            return c;
        }).toList();
        return CollectionResponse.builder().success(true).message(message).collections(colls).build();
    }

    public static CollectionResponse error(String message) {
        return CollectionResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public static CollectionResponse messageOnly(boolean success, String message) {
        return CollectionResponse.builder()
                .success(success)
                .message(message)
                .build();
    }

}
