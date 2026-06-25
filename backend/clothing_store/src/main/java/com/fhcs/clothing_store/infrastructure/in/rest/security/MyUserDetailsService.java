package com.fhcs.clothing_store.infrastructure.in.rest.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.out.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.UserBO;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepository;

    public MyUserDetailsService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserBO user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username: " + username + " not found.");
        }
        return new UserDetailsImpl(user);
    }
}
