package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.RoleBO;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.Role;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.User;

@Component
public class UserEntityMapper {

    public UserBO toBO(User entity) {
        if (entity == null) return null;
        UserBO bo = new UserBO();
        bo.setUserId(entity.getUserId());
        bo.setUsername(entity.getUsername());
        bo.setEmail(entity.getEmail());
        bo.setPasswordHash(entity.getPasswordHash());
        bo.setActive(entity.isActive());
        if (entity.getRoles() != null) {
            bo.setRoles(entity.getRoles().stream()
                    .map(this::roleToBO)
                    .collect(Collectors.toSet()));
        }
        return bo;
    }

    public User toEntity(UserBO bo) {
        if (bo == null) return null;
        User entity = new User();
        entity.setUserId(bo.getUserId());
        entity.setUsername(bo.getUsername());
        entity.setEmail(bo.getEmail());
        entity.setPasswordHash(bo.getPasswordHash());
        entity.setActive(bo.isActive());
        if (bo.getRoles() != null) {
            entity.setRoles(bo.getRoles().stream()
                    .map(this::roleToEntity)
                    .collect(Collectors.toSet()));
        }
        return entity;
    }

    public RoleBO roleToBO(Role entity) {
        if (entity == null) return null;
        RoleBO bo = new RoleBO();
        bo.setRoleId(entity.getRoleId());
        bo.setRoleName(entity.getRoleName());
        bo.setDescription(entity.getDescription());
        return bo;
    }

    public Role roleToEntity(RoleBO bo) {
        if (bo == null) return null;
        Role entity = new Role();
        entity.setRoleId(bo.getRoleId());
        entity.setRoleName(bo.getRoleName());
        entity.setDescription(bo.getDescription());
        return entity;
    }
}
