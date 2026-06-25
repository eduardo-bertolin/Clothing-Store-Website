package com.fhcs.clothing_store.application.port.out;

import com.fhcs.clothing_store.core.domain.bo.RoleBO;

public interface RoleRepositoryPort {
    RoleBO findByRoleName(String roleName);
}
