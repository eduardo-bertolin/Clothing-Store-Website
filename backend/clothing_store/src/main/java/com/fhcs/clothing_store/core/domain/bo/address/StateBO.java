package com.fhcs.clothing_store.core.domain.bo.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateBO {
    private Integer stateId;
    private String stateName;
    private String uf;
}
