package com.fhcs.clothing_store.core.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleBO {
    private Integer roleId;
    private String roleName;
    private String description;
}
