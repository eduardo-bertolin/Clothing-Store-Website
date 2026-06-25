package com.fhcs.clothing_store.infrastructure.in.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.address.IndividualAddressBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.AddressPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.PrivateIndividualPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.AddressRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.PrivateIndividualRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.AddressDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.AddressResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.PrivateIndividualResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.PrivateIndividualDtoMapper;
import com.fhcs.clothing_store.util.JsonPatchUtil;
import com.github.fge.jsonpatch.JsonPatch;

@RestController
@RequestMapping("api/private-individuals")
public class PrivateIndividualControllerAdapter {

    private final PrivateIndividualServicePort individualService;
    private final JsonPatchUtil jsonPatchUtil;
    private final PrivateIndividualDtoMapper individualMapper;

    public PrivateIndividualControllerAdapter(PrivateIndividualServicePort individualService,
            JsonPatchUtil jsonPatchUtil, PrivateIndividualDtoMapper individualMapper) {
        this.individualService = individualService;
        this.jsonPatchUtil = jsonPatchUtil;
        this.individualMapper = individualMapper;
    }

    @PostMapping
    public ResponseEntity<PrivateIndividualResponse> createPrivateIndividual(
            @RequestHeader("Authorization") String token,
            @RequestBody PrivateIndividualRequest request) {
        try {
            PrivateIndividualBO individual =
                    individualService.createPrivateIndividual(token.substring(7), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(individualMapper.toPrivateIndividualResponse(individual,
                            "Pessoa física criada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PrivateIndividualResponse.error("Erro ao criar pessoa física: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<PrivateIndividualResponse> getPrivateIndividualInformation(
            @RequestHeader("Authorization") String token) {
        try {
            PrivateIndividualBO individual =
                    individualService.getPrivateIndividualByToken(token.substring(7));
            return ResponseEntity.ok(individualMapper.toPrivateIndividualResponse(individual,
                    "Pessoa física recuperada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PrivateIndividualResponse.error(
                            "Erro ao recuperar pessoa física: " + e.getMessage()));
        }
    }

    @PatchMapping("/me")
    public ResponseEntity<PrivateIndividualResponse> updatePrivateIndividualInformation(
            @RequestHeader("Authorization") String token, @RequestBody JsonPatch patch) {
        try {
            PrivateIndividualPatchDto patchDto =
                    jsonPatchUtil.extractPatchedFields(patch, PrivateIndividualPatchDto.class);
            PrivateIndividualBO individual =
                    individualService.updatePrivateIndividual(token.substring(7), patchDto);
            return ResponseEntity.ok(individualMapper.toPrivateIndividualResponse(individual,
                    "Indivíduo atualizado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PrivateIndividualResponse.error(
                            "Erro ao atualizar indivíduo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<PrivateIndividualResponse> deletePrivateIndividual(
            @RequestHeader("Authorization") String token) {
        try {
            individualService.deletePrivateIndividual(token.substring(7));
            return ResponseEntity.ok(PrivateIndividualResponse.messageOnly(
                    "Indivíduo deletado com sucesso!", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PrivateIndividualResponse.error(
                            "Erro ao deletar indivíduo: " + e.getMessage()));
        }
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<AddressResponse> addAddress(@RequestHeader("Authorization") String token,
            @RequestBody AddressRequest request) {
        List<IndividualAddressBO> addresses =
                individualService.addAddress(token.substring(7), request);
        List<AddressDto> dtos = individualMapper.toAddressDtoList(addresses);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddressResponse.success(dtos, "Endereço criado com sucesso"));
    }

    @GetMapping("/me/addresses")
    public ResponseEntity<AddressResponse> getAddresses(
            @RequestHeader("Authorization") String token) {
        try {
            List<IndividualAddressBO> addresses =
                    individualService.getAddresses(token.substring(7));
            return ResponseEntity.ok(AddressResponse.success(
                    individualMapper.toAddressDtoList(addresses), "Endereços recuperados com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AddressResponse.error("Erro ao recuperar endereços: " + e.getMessage()));
        }
    }

    @PatchMapping("/me/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @RequestHeader("Authorization") String token, @RequestBody JsonPatch patch,
            @PathVariable Integer addressId) {
        try {
            AddressPatchDto patchDto =
                    jsonPatchUtil.extractPatchedFields(patch, AddressPatchDto.class);
            List<IndividualAddressBO> addresses =
                    individualService.updateAddress(token.substring(7), patchDto, addressId);
            return ResponseEntity.ok(AddressResponse.success(
                    individualMapper.toAddressDtoList(addresses), "Endereço atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AddressResponse.error("Erro ao atualizar o endereço: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<AddressResponse> deleteAddress(
            @RequestHeader("Authorization") String token, @PathVariable Integer addressId) {
        try {
            List<IndividualAddressBO> addresses =
                    individualService.deleteAddress(token.substring(7), addressId);
            return ResponseEntity.ok(AddressResponse.success(
                    individualMapper.toAddressDtoList(addresses), "Endereço deletado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AddressResponse.error("Erro ao deletar o endereço: " + e.getMessage()));
        }
    }
}
