package com.fhcs.clothing_store.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.out.ViaCepPort;
import com.fhcs.clothing_store.application.port.out.address.AddressRepositoryPort;
import com.fhcs.clothing_store.application.port.out.address.CepRepositoryPort;
import com.fhcs.clothing_store.application.port.out.address.CityRepositoryPort;
import com.fhcs.clothing_store.application.port.out.address.IndividualAddressRepositoryPort;
import com.fhcs.clothing_store.application.port.out.address.StateRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.ViaCepDataBO;
import com.fhcs.clothing_store.core.domain.bo.address.AddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.CepBO;
import com.fhcs.clothing_store.core.domain.bo.address.CityBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.core.domain.bo.address.StateBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.AddressPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.AddressRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.handler.AddressNotFoundException;
import com.fhcs.clothing_store.infrastructure.in.rest.handler.AddressValidationException;

@Service
public class AddressUseCase {

    private final ViaCepPort viaCepPort;
    private final AddressRepositoryPort addressRepository;
    private final CepRepositoryPort cepRepository;
    private final CityRepositoryPort cityRepository;
    private final StateRepositoryPort stateRepository;
    private final IndividualAddressRepositoryPort individualAddressRepository;

    public AddressUseCase(ViaCepPort viaCepPort, AddressRepositoryPort addressRepository,
            CepRepositoryPort cepRepository, CityRepositoryPort cityRepository,
            StateRepositoryPort stateRepository,
            IndividualAddressRepositoryPort individualAddressRepository) {
        this.viaCepPort = viaCepPort;
        this.addressRepository = addressRepository;
        this.cepRepository = cepRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.individualAddressRepository = individualAddressRepository;
    }

    public AddressBO createAddress(AddressRequest request) {
        ViaCepDataBO viaCepData = viaCepPort.fetchAddressByCep(request.getCep());

        if (Boolean.TRUE.equals(viaCepData.getError())) {
            throw new AddressNotFoundException("CEP não encontrado.");
        }

        validateAddress(request, viaCepData);

        CityBO city = cityRepository.findByCityName(request.getCity());
        StateBO state = stateRepository.findByUf(request.getStateUF());
        CepBO cep = findOrCreateCEP(request.getCep());

        AddressBO address = AddressBO.builder()
                .cep(cep)
                .state(state)
                .city(city)
                .streetName(request.getStreetName())
                .number(request.getNumber())
                .complement(request.getComplement() != null && !request.getComplement().isBlank()
                        ? request.getComplement()
                        : null)
                .build();

        return addressRepository.save(address);
    }

    public IndividualAddressBO linkIndividualAndAddress(PrivateIndividualBO individual,
            AddressBO address, String description) {
        IndividualAddressBO individualAddress = IndividualAddressBO.builder()
                .address(address)
                .description(description)
                .build();
        return individualAddressRepository.save(individualAddress);
    }

    public List<IndividualAddressBO> getIndividualAddresses(Integer individualId) {
        return addressRepository.findIndividualAddressesByIndividualId(individualId);
    }

    public AddressBO updateAddressInformation(AddressBO address, AddressPatchDto patch) {
        applyChangesToAddress(patch, address);
        return addressRepository.save(address);
    }

    public void deleteIndividualAddress(IndividualAddressBO individualAddress) {
        individualAddressRepository.delete(individualAddress);
    }

    public void applyChangesToIndividualAddress(IndividualAddressBO individualAddress,
            AddressPatchDto patch) {
        if (patch.getDescription() != null) {
            individualAddress.setDescription(patch.getDescription());
            individualAddressRepository.save(individualAddress);
        }
    }

    private void applyChangesToAddress(AddressPatchDto patch, AddressBO address) {
        if (patch.getCep() != null) {
            String cleanCep = patch.getCep().replace("-", "");
            ViaCepDataBO viaCepData = viaCepPort.fetchAddressByCep(cleanCep);

            if (Boolean.TRUE.equals(viaCepData.getError())) {
                throw new AddressNotFoundException("CEP não encontrado.");
            }

            try {
                validateChangesWithViaCepData(patch, viaCepData);
                CepBO cep = findOrCreateCEP(cleanCep);
                address.setCep(cep);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao aplicar mudanças ao endereço: " + e.getMessage());
            }
        }

        if (patch.getStreet() != null) {
            address.setStreetName(patch.getStreet());
        }
        if (patch.getNumber() != null) {
            address.setNumber(patch.getNumber());
        }
        if (patch.getCity() != null) {
            CityBO city = cityRepository.findByCityName(patch.getCity());
            address.setCity(city);
        }
        if (patch.getState() != null) {
            StateBO state = stateRepository.findByUf(patch.getState());
            address.setState(state);
        }
        if (patch.getComplement() != null) {
            address.setComplement(patch.getComplement());
        }
    }

    private void validateChangesWithViaCepData(AddressPatchDto patch, ViaCepDataBO viaCepData) {
        StringBuilder errors = new StringBuilder();
        if ((patch.getStreet() != null && !patch.getStreet().equalsIgnoreCase(viaCepData.getStreet()))
                || patch.getStreet() == null) {
            errors.append("O nome da rua não corresponde ao CEP fornecido. Rua esperada: ")
                    .append(viaCepData.getStreet()).append(". ");
        }
        if ((patch.getCity() != null && !patch.getCity().equalsIgnoreCase(viaCepData.getCity()))
                || patch.getCity() == null) {
            errors.append("A cidade não corresponde ao CEP fornecido. Cidade esperada: ")
                    .append(viaCepData.getCity()).append(". ");
        }
        if ((patch.getState() != null && !patch.getState().equalsIgnoreCase(viaCepData.getState()))
                || patch.getState() == null) {
            errors.append("O estado não corresponde ao CEP fornecido. Estado esperado")
                    .append(viaCepData.getState()).append(". ");
        }
        if (errors.length() > 0) {
            throw new AddressValidationException("Falha na validação do Endereço: " + errors);
        }
    }

    private void validateAddress(AddressRequest request, ViaCepDataBO viaCepData) {
        StringBuilder errors = new StringBuilder();
        if (!request.getStreetName().equalsIgnoreCase(viaCepData.getStreet())) {
            errors.append("O nome da rua não corresponde ao CEP fornecido. Rua esperada: ")
                    .append(viaCepData.getStreet()).append(". ");
        }
        if (!request.getCity().equalsIgnoreCase(viaCepData.getCity())) {
            errors.append("A cidade não corresponde ao CEP fornecido. Cidade esperada: ")
                    .append(viaCepData.getCity()).append(". ");
        }
        if (!request.getStateUF().equalsIgnoreCase(viaCepData.getState())) {
            errors.append("O estado não corresponde ao CEP fornecido. Estado esperado")
                    .append(viaCepData.getState()).append(". ");
        }
        if (errors.length() > 0) {
            throw new AddressValidationException("Falha na validação do Endereço: " + errors);
        }
    }

    private CepBO findOrCreateCEP(String cepNumber) {
        String clean = cepNumber.replace("-", "");
        CepBO cep = cepRepository.findByCepNumber(clean);
        if (cep == null) {
            cep = new CepBO();
            cep.setCepNumber(clean);
            cep = cepRepository.save(cep);
        }
        return cep;
    }
}
