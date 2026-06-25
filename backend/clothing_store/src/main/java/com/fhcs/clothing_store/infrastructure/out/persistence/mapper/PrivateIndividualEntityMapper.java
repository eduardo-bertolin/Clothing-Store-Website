package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.PrivateIndividual;

@Component
public class PrivateIndividualEntityMapper {

    private final UserEntityMapper userMapper;
    private final AddressEntityMapper addressMapper;

    public PrivateIndividualEntityMapper(UserEntityMapper userMapper,
            AddressEntityMapper addressMapper) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
    }

    public PrivateIndividualBO toBO(PrivateIndividual entity) {
        if (entity == null) return null;
        PrivateIndividualBO bo = new PrivateIndividualBO();
        bo.setIndividualId(entity.getIndividualId());
        bo.setUser(userMapper.toBO(entity.getUser()));
        bo.setIndividualName(entity.getIndividualName());
        bo.setBirthDate(entity.getBirthDate());
        bo.setCPF(entity.getCPF());
        bo.setPhoneNumber(entity.getPhoneNumber());
        if (entity.getIndividualAddress() != null) {
            bo.setIndividualAddress(entity.getIndividualAddress().stream()
                    .map(addressMapper::individualAddressToBO)
                    .collect(Collectors.toList()));
        }
        return bo;
    }

    public PrivateIndividual toEntity(PrivateIndividualBO bo) {
        if (bo == null) return null;
        PrivateIndividual entity = new PrivateIndividual();
        entity.setIndividualId(bo.getIndividualId());
        entity.setUser(userMapper.toEntity(bo.getUser()));
        entity.setIndividualName(bo.getIndividualName());
        entity.setBirthDate(bo.getBirthDate());
        entity.setCPF(bo.getCPF());
        entity.setPhoneNumber(bo.getPhoneNumber());
        return entity;
    }
}
