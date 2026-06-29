package com.fhcs.clothing_store.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fhcs.clothing_store.application.port.out.ViaCepPort;
import com.fhcs.clothing_store.application.port.out.persistence.address.AddressRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.address.CepRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.address.CityRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.address.IndividualAddressRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.address.StateRepositoryPort;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AddressUseCaseTest {

    @Mock ViaCepPort viaCepPort;
    @Mock AddressRepositoryPort addressRepository;
    @Mock CepRepositoryPort cepRepository;
    @Mock CityRepositoryPort cityRepository;
    @Mock StateRepositoryPort stateRepository;
    @Mock IndividualAddressRepositoryPort individualAddressRepository;

    @InjectMocks AddressUseCase addressUseCase;

    private ViaCepDataBO validViaCepData;
    private AddressRequest request;

    @BeforeEach
    void setUp() {
        validViaCepData = new ViaCepDataBO();
        validViaCepData.setStreet("Avenida Paulista");
        validViaCepData.setCity("São Paulo");
        validViaCepData.setState("SP");

        request = mock(AddressRequest.class);
        when(request.getCep()).thenReturn("01310-100");
        when(request.getStreetName()).thenReturn("Avenida Paulista");
        when(request.getCity()).thenReturn("São Paulo");
        when(request.getStateUF()).thenReturn("SP");
        when(request.getNumber()).thenReturn(1000);
    }

    // ── createAddress ──────────────────────────────────────────────────────────

    @Test
    void createAddress_savesAddressWhenDataMatchesCep() {
        AddressBO savedAddress = AddressBO.builder().addressId(1).build();

        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(validViaCepData);
        when(cityRepository.findByCityName("São Paulo")).thenReturn(new CityBO());
        when(stateRepository.findByUf("SP")).thenReturn(new StateBO());
        when(cepRepository.findByCepNumber("01310100")).thenReturn(new CepBO(1, "01310100"));
        when(addressRepository.save(any(AddressBO.class))).thenReturn(savedAddress);

        AddressBO result = addressUseCase.createAddress(request);

        assertThat(result).isEqualTo(savedAddress);
        verify(addressRepository).save(any(AddressBO.class));
    }

    @Test
    void createAddress_createsCepWhenNotExistsInDatabase() {
        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(validViaCepData);
        when(cityRepository.findByCityName("São Paulo")).thenReturn(new CityBO());
        when(stateRepository.findByUf("SP")).thenReturn(new StateBO());
        when(cepRepository.findByCepNumber("01310100")).thenReturn(null);
        when(cepRepository.save(any(CepBO.class))).thenReturn(new CepBO(1, "01310100"));
        when(addressRepository.save(any(AddressBO.class))).thenReturn(AddressBO.builder().build());

        addressUseCase.createAddress(request);

        verify(cepRepository).save(any(CepBO.class));
    }

    @Test
    void createAddress_throwsWhenCepIsInvalid() {
        ViaCepDataBO errorResponse = new ViaCepDataBO();
        errorResponse.setError(true);
        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(errorResponse);

        assertThatThrownBy(() -> addressUseCase.createAddress(request))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("CEP não encontrado");
    }

    @Test
    void createAddress_throwsWhenStreetNameDoesNotMatchCep() {
        when(request.getStreetName()).thenReturn("Rua Errada");
        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(validViaCepData);

        assertThatThrownBy(() -> addressUseCase.createAddress(request))
                .isInstanceOf(AddressValidationException.class)
                .hasMessageContaining("Falha na validação");
    }

    @Test
    void createAddress_throwsWhenCityDoesNotMatchCep() {
        when(request.getCity()).thenReturn("Campinas");
        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(validViaCepData);

        assertThatThrownBy(() -> addressUseCase.createAddress(request))
                .isInstanceOf(AddressValidationException.class)
                .hasMessageContaining("Falha na validação");
    }

    @Test
    void createAddress_throwsWhenStateDoesNotMatchCep() {
        when(request.getStateUF()).thenReturn("RJ");
        when(viaCepPort.fetchAddressByCep("01310-100")).thenReturn(validViaCepData);

        assertThatThrownBy(() -> addressUseCase.createAddress(request))
                .isInstanceOf(AddressValidationException.class)
                .hasMessageContaining("Falha na validação");
    }

    // ── linkIndividualAndAddress ────────────────────────────────────────────────

    @Test
    void linkIndividualAndAddress_savesAndReturnsLink() {
        IndividualAddressBO saved = IndividualAddressBO.builder().individualAddressId(1).build();
        when(individualAddressRepository.save(any(IndividualAddressBO.class))).thenReturn(saved);

        IndividualAddressBO result = addressUseCase.linkIndividualAndAddress(
                new PrivateIndividualBO(), AddressBO.builder().build(), "Casa");

        assertThat(result).isEqualTo(saved);
        verify(individualAddressRepository).save(any(IndividualAddressBO.class));
    }

    // ── getIndividualAddresses ─────────────────────────────────────────────────

    @Test
    void getIndividualAddresses_returnsListFromRepository() {
        List<IndividualAddressBO> addresses = List.of(new IndividualAddressBO(), new IndividualAddressBO());
        when(addressRepository.findIndividualAddressesByIndividualId(1)).thenReturn(addresses);

        List<IndividualAddressBO> result = addressUseCase.getIndividualAddresses(1);

        assertThat(result).hasSize(2);
    }

    // ── updateAddressInformation ───────────────────────────────────────────────

    @Test
    void updateAddressInformation_updatesFieldsWithoutCepChange() {
        AddressBO address = AddressBO.builder().addressId(1).build();
        AddressPatchDto patch = new AddressPatchDto();
        patch.setStreet("Nova Rua");
        patch.setNumber(42);

        when(addressRepository.save(address)).thenReturn(address);

        addressUseCase.updateAddressInformation(address, patch);

        assertThat(address.getStreetName()).isEqualTo("Nova Rua");
        assertThat(address.getNumber()).isEqualTo(42);
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddressInformation_validatesCepWithViaCepWhenCepChanges() {
        AddressBO address = AddressBO.builder().addressId(1).build();
        AddressPatchDto patch = new AddressPatchDto();
        patch.setCep("01310-100");
        patch.setStreet("Avenida Paulista");
        patch.setCity("São Paulo");
        patch.setState("SP");

        when(viaCepPort.fetchAddressByCep("01310100")).thenReturn(validViaCepData);
        when(cepRepository.findByCepNumber("01310100")).thenReturn(new CepBO(1, "01310100"));
        when(addressRepository.save(address)).thenReturn(address);

        addressUseCase.updateAddressInformation(address, patch);

        verify(viaCepPort).fetchAddressByCep("01310100");
    }

    @Test
    void updateAddressInformation_throwsWhenNewCepNotFound() {
        AddressBO address = AddressBO.builder().addressId(1).build();
        AddressPatchDto patch = new AddressPatchDto();
        patch.setCep("00000-000");

        ViaCepDataBO errorResponse = new ViaCepDataBO();
        errorResponse.setError(true);
        when(viaCepPort.fetchAddressByCep("00000000")).thenReturn(errorResponse);

        assertThatThrownBy(() -> addressUseCase.updateAddressInformation(address, patch))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining("CEP não encontrado");
    }

    // ── deleteIndividualAddress ────────────────────────────────────────────────

    @Test
    void deleteIndividualAddress_delegatesToRepository() {
        IndividualAddressBO individualAddress = new IndividualAddressBO();

        addressUseCase.deleteIndividualAddress(individualAddress);

        verify(individualAddressRepository).delete(individualAddress);
    }

    // ── applyChangesToIndividualAddress ───────────────────────────────────────

    @Test
    void applyChangesToIndividualAddress_updatesDescriptionWhenProvided() {
        IndividualAddressBO individualAddress = new IndividualAddressBO();
        AddressPatchDto patch = new AddressPatchDto();
        patch.setDescription("Trabalho");

        addressUseCase.applyChangesToIndividualAddress(individualAddress, patch);

        assertThat(individualAddress.getDescription()).isEqualTo("Trabalho");
        verify(individualAddressRepository).save(individualAddress);
    }

    @Test
    void applyChangesToIndividualAddress_doesNothingWhenDescriptionIsNull() {
        IndividualAddressBO individualAddress = new IndividualAddressBO();
        AddressPatchDto patch = new AddressPatchDto();

        addressUseCase.applyChangesToIndividualAddress(individualAddress, patch);

        verify(individualAddressRepository, never()).save(any());
    }
}
