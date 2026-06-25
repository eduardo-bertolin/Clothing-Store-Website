package com.fhcs.clothing_store.infrastructure.in.rest.dto.response;

import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.PrivateIndividual;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PrivateIndividualDto {

    private Integer individualId;
    private String individualName;
    private String maskedCpf;
    private String phoneNumber;
    private UserDto user;

    public static PrivateIndividualDto fromIndividual(PrivateIndividual individual) {
        String cpf = individual.getCPF();
        String maskedCpf = "XXX." + cpf.substring(3, 6) + ".XXX-" + cpf.substring(9);
        return PrivateIndividualDto.builder()
                .individualId(individual.getIndividualId())
                .individualName(individual.getIndividualName())
                .maskedCpf(maskedCpf)
                .phoneNumber(individual.getPhoneNumber())
                .user(UserDto.fromUser(individual.getUser()))
                .build();
    }

    public static PrivateIndividualDto fromBO(PrivateIndividualBO individual) {
        String cpf = individual.getCPF();
        String maskedCpf = "XXX." + cpf.substring(3, 6) + ".XXX-" + cpf.substring(9);
        return PrivateIndividualDto.builder()
                .individualId(individual.getIndividualId())
                .individualName(individual.getIndividualName())
                .maskedCpf(maskedCpf)
                .phoneNumber(individual.getPhoneNumber())
                .user(UserDto.fromBO(individual.getUser()))
                .build();
    }
}
