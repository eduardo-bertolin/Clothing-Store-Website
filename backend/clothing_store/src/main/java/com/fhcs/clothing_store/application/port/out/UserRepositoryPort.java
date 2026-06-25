package com.fhcs.clothing_store.application.port.out;

import com.fhcs.clothing_store.core.domain.bo.UserBO;

public interface UserRepositoryPort {
    UserBO save(UserBO user);
    UserBO findByEmail(String email);
    UserBO findByUsername(String username);
    void delete(UserBO user);
}
