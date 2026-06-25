package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.RoleRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.RoleBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.RoleRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.UserEntityMapper;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleRepository roleRepository;
    private final UserEntityMapper mapper;

    public RoleRepositoryAdapter(RoleRepository roleRepository, UserEntityMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @Override
    public RoleBO findByRoleName(String roleName) {
        return mapper.roleToBO(roleRepository.findByRoleName(roleName));
    }
}
