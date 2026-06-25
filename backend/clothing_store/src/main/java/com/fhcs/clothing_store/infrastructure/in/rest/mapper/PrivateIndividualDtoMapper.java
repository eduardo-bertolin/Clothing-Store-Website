package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.AddressDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.PrivateIndividualDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.PrivateIndividualResponse;

@Component
public class PrivateIndividualDtoMapper {

    private final UserDtoMapper userMapper;

    public PrivateIndividualDtoMapper(UserDtoMapper userMapper) {
        this.userMapper = userMapper;
    }

    public PrivateIndividualDto toPrivateIndividualDto(PrivateIndividualBO bo) {
        String cpf = bo.getCPF();
        String masked = "XXX." + cpf.substring(3, 6) + ".XXX-" + cpf.substring(9);
        return PrivateIndividualDto.builder()
                .individualId(bo.getIndividualId())
                .individualName(bo.getIndividualName())
                .maskedCpf(masked)
                .phoneNumber(bo.getPhoneNumber())
                .user(userMapper.toUserDto(bo.getUser()))
                .build();
    }

    public PrivateIndividualResponse toPrivateIndividualResponse(PrivateIndividualBO bo, String message) {
        return PrivateIndividualResponse.builder()
                .safeIndividual(toPrivateIndividualDto(bo))
                .success(true)
                .message(message)
                .build();
    }

    public AddressDto toAddressDto(IndividualAddressBO ia) {
        AddressBO addr = ia.getAddress();
        String cep = addr.getCep().getCepNumber();
        String formattedCep = cep.replaceAll("(\\d{5})", "$1-");
        String complement = addr.getComplement() != null ? addr.getComplement() : "";
        return AddressDto.builder()
                .id(addr.getAddressId())
                .street(addr.getStreetName())
                .number(addr.getNumber())
                .city(addr.getCity().getCityName())
                .state(addr.getState().getStateName())
                .cep(formattedCep)
                .complement(complement)
                .description(ia.getDescription())
                .build();
    }

    public List<AddressDto> toAddressDtoList(List<IndividualAddressBO> addresses) {
        return addresses.stream().map(this::toAddressDto).toList();
    }
}
