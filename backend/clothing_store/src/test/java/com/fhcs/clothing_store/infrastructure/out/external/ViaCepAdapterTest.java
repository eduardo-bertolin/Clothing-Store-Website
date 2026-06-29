package com.fhcs.clothing_store.infrastructure.out.external;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.fhcs.clothing_store.core.domain.bo.ViaCepDataBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.ViaCepResponse;

@ExtendWith(MockitoExtension.class)
class ViaCepAdapterTest {

    private static final String BASE_URL = "https://viacep.com.br/ws/";

    @Mock RestTemplate restTemplate;

    @InjectMocks ViaCepAdapter viaCepAdapter;

    // ── fetchAddressByCep ──────────────────────────────────────────────────────

    @Test
    void fetchAddressByCep_mapsAllFieldsFromResponse() {
        ViaCepResponse response = new ViaCepResponse();
        response.setCep("01310-100");
        response.setStreet("Avenida Paulista");
        response.setComplement("");
        response.setNeighborhood("Bela Vista");
        response.setCity("São Paulo");
        response.setState("SP");
        response.setIbgeCode("3550308");

        when(restTemplate.getForObject(BASE_URL + "01310100/json/", ViaCepResponse.class)).thenReturn(response);

        ViaCepDataBO result = viaCepAdapter.fetchAddressByCep("01310100");

        assertThat(result.getCep()).isEqualTo("01310-100");
        assertThat(result.getStreet()).isEqualTo("Avenida Paulista");
        assertThat(result.getNeighborhood()).isEqualTo("Bela Vista");
        assertThat(result.getCity()).isEqualTo("São Paulo");
        assertThat(result.getState()).isEqualTo("SP");
        assertThat(result.getIbgeCode()).isEqualTo("3550308");
        assertThat(result.getError()).isNull();
    }

    @Test
    void fetchAddressByCep_returnsErrorBoWhenRestTemplateReturnsNull() {
        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class))).thenReturn(null);

        ViaCepDataBO result = viaCepAdapter.fetchAddressByCep("00000000");

        assertThat(result.getError()).isTrue();
    }

    @Test
    void fetchAddressByCep_mapsErrorFlagWhenCepDoesNotExist() {
        ViaCepResponse errorResponse = new ViaCepResponse();
        errorResponse.setError(true);

        when(restTemplate.getForObject(anyString(), eq(ViaCepResponse.class))).thenReturn(errorResponse);

        ViaCepDataBO result = viaCepAdapter.fetchAddressByCep("00000000");

        assertThat(result.getError()).isTrue();
    }
}
