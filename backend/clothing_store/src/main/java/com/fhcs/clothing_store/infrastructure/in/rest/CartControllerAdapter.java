package com.fhcs.clothing_store.infrastructure.in.rest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.CartServicePort;
import com.fhcs.clothing_store.application.port.in.service.OrderServicePort;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CartItemRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CheckoutRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.cart.CartResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.CartDtoMapper;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.OrderDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartControllerAdapter {

    private final CartServicePort cartService;
    private final OrderServicePort orderService;
    private final CartDtoMapper cartMapper;
    private final OrderDtoMapper orderMapper;

    public CartControllerAdapter(CartServicePort cartService, OrderServicePort orderService,
            CartDtoMapper cartMapper, OrderDtoMapper orderMapper) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getOrCreateCart(
            @RequestHeader("Authorization") String token) {
        try {
            CartBO cart = cartService.getOrCreateCart(token.substring(7));
            return ResponseEntity.ok(cartMapper.toCartResponse(cart, "Carrinho recuperado com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(cartMapper.toCartError("Erro ao recuperar carrinho: " + e.getMessage()));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestHeader("Authorization") String token,
            @Valid @RequestBody CartItemRequest request) {
        try {
            CartBO cart = cartService.addItem(token.substring(7), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(cartMapper.toCartResponse(cart, "Item adicionado ao carrinho."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(cartMapper.toCartError("Erro ao adicionar item: " + e.getMessage()));
        }
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @RequestHeader("Authorization") String token, @PathVariable Integer cartItemId,
            @RequestParam Integer quantity) {
        try {
            CartBO cart = cartService.updateItemQuantity(token.substring(7), cartItemId, quantity);
            return ResponseEntity.ok(cartMapper.toCartResponse(cart, "Item atualizado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(cartMapper.toCartError("Erro ao atualizar item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(@RequestHeader("Authorization") String token,
            @PathVariable Integer cartItemId) {
        try {
            CartBO cart = cartService.removeItem(token.substring(7), cartItemId);
            return ResponseEntity.ok(cartMapper.toCartResponse(cart, "Item removido do carrinho."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(cartMapper.toCartError("Erro ao remover item: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(@RequestHeader("Authorization") String token) {
        try {
            CartBO cart = cartService.clearCart(token.substring(7));
            return ResponseEntity.ok(cartMapper.toCartResponse(cart, "Carrinho esvaziado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(cartMapper.toCartError("Erro ao limpar carrinho: " + e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestHeader("Authorization") String token,
            @Valid @RequestBody CheckoutRequest request) {
        try {
            OrderBO order = orderService.checkout(token.substring(7), request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderMapper.toOrderResponse(order, "Pedido criado com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError("Erro ao finalizar compra: " + e.getMessage()));
        }
    }
}
