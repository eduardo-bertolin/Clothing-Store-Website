package com.fhcs.clothing_store.application.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.application.port.out.JwtPort;
import com.fhcs.clothing_store.application.port.out.PrivateIndividualRepositoryPort;
import com.fhcs.clothing_store.application.port.out.UserRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.UserBO;
import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.AddressPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.PrivateIndividualPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.AddressRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.PrivateIndividualRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.handler.AddressNotFoundException;

@Service
public class PrivateIndividualUseCase implements PrivateIndividualServicePort {

    private final PrivateIndividualRepositoryPort individualRepository;
    private final UserRepositoryPort userRepository;
    private final JwtPort jwtPort;
    private final AddressUseCase addressUseCase;

    public PrivateIndividualUseCase(PrivateIndividualRepositoryPort individualRepository,
            UserRepositoryPort userRepository, JwtPort jwtPort, AddressUseCase addressUseCase) {
        this.individualRepository = individualRepository;
        this.userRepository = userRepository;
        this.jwtPort = jwtPort;
        this.addressUseCase = addressUseCase;
    }

    @Override
    public PrivateIndividualBO createPrivateIndividual(String accessToken,
            PrivateIndividualRequest request) {
        UserBO user = userRepository.findByUsername(jwtPort.extractUsername(accessToken));
        UserBO requestUser = userRepository.findByEmail(request.getEmail());

        if (!requestUser.getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException(
                    "O email fornecido não corresponde ao usuário autenticado.");
        }

        String formattedCpf = request.getCpf().replaceAll("[^\\d]", "");
        String formattedPhone = request.getPhoneNumber().replaceAll("[^\\d]", "");

        PrivateIndividualBO individual = new PrivateIndividualBO();
        individual.setUser(requestUser);
        individual.setIndividualName(request.getIndividualName());
        individual.setBirthDate(
                LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        individual.setCPF(formattedCpf);
        individual.setPhoneNumber(formattedPhone);

        individualRepository.save(individual);

        AddressBO address = addressUseCase.createAddress(request.getAddress());
        addressUseCase.linkIndividualAndAddress(individual, address,
                request.getAddress().getDescription());

        return individual;
    }

    @Override
    public PrivateIndividualBO getPrivateIndividualByToken(String accessToken) {
        UserBO user = userRepository.findByUsername(jwtPort.extractUsername(accessToken));
        return individualRepository.findByUserId(user.getUserId());
    }

    @Override
    public PrivateIndividualBO updatePrivateIndividual(String accessToken,
            PrivateIndividualPatchDto patch) {
        try {
            PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
            applyChangesToIndividual(individual, patch);
            return individualRepository.save(individual);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar informações do indivíduo: " + e.getMessage());
        }
    }

    @Override
    public void deletePrivateIndividual(String accessToken) {
        PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
        individualRepository.delete(individual);
    }

    @Override
    public List<IndividualAddressBO> addAddress(String accessToken, AddressRequest request) {
        PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
        AddressBO address = addressUseCase.createAddress(request);
        addressUseCase.linkIndividualAndAddress(individual, address, request.getDescription());
        return addressUseCase.getIndividualAddresses(individual.getIndividualId());
    }

    @Override
    public List<IndividualAddressBO> getAddresses(String accessToken) {
        PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
        return addressUseCase.getIndividualAddresses(individual.getIndividualId());
    }

    @Override
    public List<IndividualAddressBO> updateAddress(String accessToken, AddressPatchDto patch,
            Integer addressId) {
        try {
            PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
            List<IndividualAddressBO> addresses =
                    addressUseCase.getIndividualAddresses(individual.getIndividualId());

            IndividualAddressBO individualAddress =
                    findIndividualAddressById(addresses, addressId);

            if (patch.getDescription() != null) {
                addressUseCase.applyChangesToIndividualAddress(individualAddress, patch);
            }

            addressUseCase.updateAddressInformation(individualAddress.getAddress(), patch);
            return addresses;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar o endereço do indivíduo: " + e.getMessage());
        }
    }

    @Override
    public List<IndividualAddressBO> deleteAddress(String accessToken, Integer addressId) {
        try {
            PrivateIndividualBO individual = getPrivateIndividualByToken(accessToken);
            List<IndividualAddressBO> addresses =
                    addressUseCase.getIndividualAddresses(individual.getIndividualId());

            IndividualAddressBO individualAddress = findIndividualAddressById(addresses, addressId);
            addressUseCase.deleteIndividualAddress(individualAddress);

            return addressUseCase.getIndividualAddresses(individual.getIndividualId());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar o endereço do indivíduo: " + e.getMessage());
        }
    }

    private void applyChangesToIndividual(PrivateIndividualBO individual,
            PrivateIndividualPatchDto patch) {
        try {
            if (patch.getPhoneNumber() != null) {
                String formattedPhone = patch.getPhoneNumber().replaceAll("[^\\d]", "");
                boolean exists = individualRepository.existsByPhoneNumberAndIndividualIdNot(
                        formattedPhone, individual.getIndividualId());
                if (exists) {
                    throw new IllegalArgumentException(
                            "O número de telefone já está em uso por outro indivíduo.");
                }
                individual.setPhoneNumber(formattedPhone);
            }
            if (patch.getIndividualName() != null) {
                individual.setIndividualName(patch.getIndividualName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar alterações no individuo: " + e.getMessage());
        }
    }

    private IndividualAddressBO findIndividualAddressById(List<IndividualAddressBO> addresses,
            Integer addressId) {
        return addresses.stream()
                .filter(ia -> ia.getAddress().getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException(
                        "Endereço não encontrado para o indivíduo fornecido."));
    }
}
