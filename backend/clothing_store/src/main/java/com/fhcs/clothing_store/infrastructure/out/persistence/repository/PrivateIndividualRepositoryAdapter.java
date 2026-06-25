package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.PrivateIndividualRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.PrivateIndividualRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.PrivateIndividualEntityMapper;

@Component
public class PrivateIndividualRepositoryAdapter implements PrivateIndividualRepositoryPort {

    private final PrivateIndividualRepository repository;
    private final PrivateIndividualEntityMapper mapper;

    public PrivateIndividualRepositoryAdapter(PrivateIndividualRepository repository,
            PrivateIndividualEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PrivateIndividualBO save(PrivateIndividualBO bo) {
        return mapper.toBO(repository.save(mapper.toEntity(bo)));
    }

    @Override
    public PrivateIndividualBO findByUserId(Integer userId) {
        return mapper.toBO(repository.findByUser_UserId(userId));
    }

    @Override
    public void delete(PrivateIndividualBO bo) {
        repository.delete(mapper.toEntity(bo));
    }

    @Override
    public boolean existsByPhoneNumberAndIndividualIdNot(String phoneNumber, Integer individualId) {
        return repository.existsByPhoneNumberAndIndividualIdNot(phoneNumber, individualId);
    }
}
