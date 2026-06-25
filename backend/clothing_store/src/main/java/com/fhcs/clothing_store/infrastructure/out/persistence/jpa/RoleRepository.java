package com.fhcs.clothing_store.infrastructure.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.clothing_store.infrastructure.out.persistence.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleName(String roleName);
    
}
