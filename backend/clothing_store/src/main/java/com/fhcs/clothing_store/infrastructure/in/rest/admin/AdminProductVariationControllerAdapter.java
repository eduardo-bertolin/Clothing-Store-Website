package com.fhcs.clothing_store.infrastructure.in.rest.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.admin.AdminProductVariationServicePort;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.ProductVariationPatchDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.product.variation.ProductVariationRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.variation.ProductVariationDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.product.variation.ProductVariationResponse;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;
import com.fhcs.clothing_store.util.JsonPatchUtil;
import com.github.fge.jsonpatch.JsonPatch;

@RestController
@RequestMapping("/api/admin/products/{productId}/variations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminProductVariationControllerAdapter {

    private final AdminProductVariationServicePort variationService;
    private final JsonPatchUtil jsonPatchUtil;
    private final ProductEntityMapper productMapper;

    public AdminProductVariationControllerAdapter(
            AdminProductVariationServicePort variationService,
            JsonPatchUtil jsonPatchUtil, ProductEntityMapper productMapper) {
        this.variationService = variationService;
        this.jsonPatchUtil = jsonPatchUtil;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductVariationResponse> createProductVariation(
            @PathVariable Integer productId, @RequestBody ProductVariationRequest request) {
        try {
            ProductVariationBO variation =
                    variationService.createProductVariation(productId, request);
            return ResponseEntity.ok(ProductVariationResponse.success(
                    productMapper.productToEntity(variation.getProduct()),
                    productMapper.variationToEntity(variation),
                    "Variação de produto criada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProductVariationResponse.error(
                            "Erro ao criar a variação de produto: " + e.getMessage()));
        }
    }

    @PatchMapping("/{variationId}")
    public ResponseEntity<ProductVariationResponse> updateProductVariation(
            @PathVariable Integer productId, @PathVariable Integer variationId,
            @RequestBody JsonPatch patch) {
        try {
            ProductVariationPatchDto patchDto =
                    jsonPatchUtil.extractPatchedFields(patch, ProductVariationPatchDto.class);
            ProductVariationBO variation =
                    variationService.updateProductVariation(variationId, patchDto);
            return ResponseEntity.ok(ProductVariationResponse.success(
                    productMapper.productToEntity(variation.getProduct()),
                    productMapper.variationToEntity(variation),
                    "Variação de produto atualizada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProductVariationResponse.error(
                            "Erro ao atualizar a variação de produto: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{variationId}")
    public ResponseEntity<ProductVariationResponse> deleteProductVariation(
            @PathVariable Integer variationId) {
        try {
            variationService.deleteProductVariation(variationId);
            return ResponseEntity.ok(
                    ProductVariationResponse.messageOnly(true,
                            "Variação de produto deletada com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProductVariationResponse.error(
                            "Erro ao deletar a variação de produto: " + e.getMessage()));
        }
    }
}
