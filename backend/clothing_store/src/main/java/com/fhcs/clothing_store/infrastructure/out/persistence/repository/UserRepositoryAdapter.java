package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.UserRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.UserEntityMapper;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(UserRepository userRepository, UserEntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserBO save(UserBO user) {
        return mapper.toBO(userRepository.save(mapper.toEntity(user)));
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
