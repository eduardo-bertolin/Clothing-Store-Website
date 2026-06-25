package com.fhcs.clothing_store.core.domain.bo.product;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionBO {
    private Integer collectionId;
    private String collectionName;
    private String description;
    private LocalDate launchDate;
}
