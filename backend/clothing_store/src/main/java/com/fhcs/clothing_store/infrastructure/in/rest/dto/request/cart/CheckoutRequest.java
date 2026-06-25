package com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart;
 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
 
@Getter
public class CheckoutRequest {
 
    @NotBlank(message = "O método de pagamento é obrigatório")
    private String paymentMethod;
}