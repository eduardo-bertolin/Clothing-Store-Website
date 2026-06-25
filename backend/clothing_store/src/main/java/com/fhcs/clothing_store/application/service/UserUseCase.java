package com.fhcs.clothing_store.application.service;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.UserServicePort;
import com.fhcs.clothing_store.application.port.out.JwtPort;
import com.fhcs.clothing_store.application.port.out.PasswordEncoderPort;
import com.fhcs.clothing_store.application.port.out.RoleRepositoryPort;
import com.fhcs.clothing_store.application.port.out.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.RoleBO;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.UserPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.RegisterRequest;

@Service
public class UserUseCase implements UserServicePort {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final JwtPort jwtPort;
    private final PasswordEncoderPort passwordEncoder;

    public UserUseCase(UserRepositoryPort userRepository, RoleRepositoryPort roleRepository,
            JwtPort jwtPort, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtPort = jwtPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserBO createUser(RegisterRequest request) {
        UserBO user = new UserBO();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        RoleBO userRole = roleRepository.findByRoleName("ROLE_USER");
        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    @Override
    public UserBO getUserByToken(String accessToken) throws Exception {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Token é obrigatório");
        }
        String username = jwtPort.extractUsername(accessToken);
        UserBO user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return user;
    }

    @Override
    public UserBO updateUser(String accessToken, UserPatchDto patch) throws Exception {
        try {
            UserBO user = getUserByToken(accessToken);
            verifyUsernameAndEmailUniqueness(user, patch);

            if (patch.getUsername() != null && !patch.getUsername().isEmpty()) {
                user.setUsername(patch.getUsername());
            }
            if (patch.getEmail() != null && !patch.getEmail().isEmpty()) {
                user.setEmail(patch.getEmail());
            }

            String newPassword = patch.getPasswordHash();
            if (newPassword != null && !newPassword.isEmpty()
                    && !passwordEncoder.matches(newPassword, user.getPasswordHash())) {
                user.setPasswordHash(passwordEncoder.encode(newPassword));
            }

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar informações do usuário: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String accessToken) throws Exception {
        UserBO user = getUserByToken(accessToken);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    private void verifyUsernameAndEmailUniqueness(UserBO user, UserPatchDto patch) {
        if (patch.getUsername() != null && !patch.getUsername().isEmpty()) {
            if (existsByUsername(patch.getUsername())) {
                throw new IllegalArgumentException("Nome de usuário já está em uso");
            }
        }
        if (patch.getEmail() != null && !patch.getEmail().isEmpty()) {
            if (existsByEmail(patch.getEmail())) {
                throw new IllegalArgumentException("Email já está em uso");
            }
        }
    }
}
