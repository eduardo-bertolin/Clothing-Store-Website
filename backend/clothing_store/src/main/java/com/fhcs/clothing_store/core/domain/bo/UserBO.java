package com.fhcs.clothing_store.core.domain.bo;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {
    private Integer userId;
    private String username;
    private String email;
    private String passwordHash;
    private Set<RoleBO> roles = new HashSet<>();
    private boolean active = true;
}
