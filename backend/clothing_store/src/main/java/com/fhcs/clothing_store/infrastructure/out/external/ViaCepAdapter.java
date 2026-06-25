package com.fhcs.clothing_store.infrastructure.out.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fhcs.clothing_store.application.port.out.ViaCepPort;
import com.fhcs.clothing_store.core.domain.bo.ViaCepDataBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.ViaCepResponse;

@Component
public class ViaCepAdapter implements ViaCepPort {

    private static final String VIACEP_API_URL = "https://viacep.com.br/ws/";

    private final RestTemplate restTemplate;

    public ViaCepAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ViaCepDataBO fetchAddressByCep(String cep) {
        String url = VIACEP_API_URL + cep + "/json/";
        ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);

        if (response == null) {
            ViaCepDataBO error = new ViaCepDataBO();
            error.setError(true);
            return error;
        }

        ViaCepDataBO bo = new ViaCepDataBO();
        bo.setCep(response.getCep());
        bo.setStreet(response.getStreet());
        bo.setComplement(response.getComplement());
        bo.setNeighborhood(response.getNeighborhood());
        bo.setCity(response.getCity());
        bo.setState(response.getState());
        bo.setIbgeCode(response.getIbgeCode());
        bo.setError(response.getError());
        return bo;
    }
}
