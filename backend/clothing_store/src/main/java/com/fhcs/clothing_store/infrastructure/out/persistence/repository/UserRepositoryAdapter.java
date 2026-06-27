package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.persistence.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.Role;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.User;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.RoleRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.UserRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.UserEntityMapper;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(UserRepository userRepository, UserEntityMapper mapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserBO save(UserBO user) {
        User entity = mapper.toEntity(user);

        Set<Role> managedRoles = entity.getRoles().stream()
            .map(role -> roleRepository.findById(role.getRoleId()).orElseThrow())
            .collect(Collectors.toSet());
        entity.setRoles(managedRoles);
        return mapper.toBO(userRepository.save(entity));
    }

    @Override
    public UserBO findByEmail(String email) {
        return mapper.toBO(userRepository.findByEmail(email));
    }

    @Override
    public UserBO findByUsername(String username) {
        return mapper.toBO(userRepository.findByUsername(username));
    }

    @Override
    public void delete(UserBO user) {
        userRepository.delete(mapper.toEntity(user));
    }
}
