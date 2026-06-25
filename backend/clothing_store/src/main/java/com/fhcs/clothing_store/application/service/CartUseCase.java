package com.fhcs.clothing_store.application.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhcs.clothing_store.application.port.in.service.CartServicePort;
import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.application.port.out.cart.CartItemRepositoryPort;
import com.fhcs.clothing_store.application.port.out.cart.CartRepositoryPort;
import com.fhcs.clothing_store.application.port.out.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartStatusBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CartItemRequest;

@Service
@Transactional
public class CartUseCase implements CartServicePort {

    private final CartRepositoryPort cartRepository;
    private final CartItemRepositoryPort cartItemRepository;
    private final ProductVariationRepositoryPort variationRepository;
    private final PrivateIndividualServicePort individualService;

    public CartUseCase(CartRepositoryPort cartRepository, CartItemRepositoryPort cartItemRepository,
            ProductVariationRepositoryPort variationRepository,
            PrivateIndividualServicePort individualService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variationRepository = variationRepository;
        this.individualService = individualService;
    }

    @Override
    public CartBO getOrCreateCart(String accessToken) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        return findOrCreateActiveCart(individual);
    }

    @Override
    public CartBO addItem(String accessToken, CartItemRequest request) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        CartBO cart = findOrCreateActiveCart(individual);

        ProductVariationBO variation = variationRepository.findById(request.getVariationId())
                .orElseThrow(() -> new IllegalArgumentException("Variação não encontrada."));

        validateStock(variation, request.getQuantity());

        cartItemRepository.findByCartIdAndVariationId(cart.getCartId(), variation.getVariationId())
                .ifPresentOrElse(
                        existing -> {
                            int newQty = existing.getQuantity() + request.getQuantity();
                            validateStock(variation, newQty);
                            existing.setQuantity(newQty);
                            cartItemRepository.save(existing);
                        },
                        () -> {
                            CartItemBO item = CartItemBO.builder()
                                    .cartId(cart.getCartId())
                                    .variation(variation)
                                    .quantity(request.getQuantity())
                                    .unitPrice(variation.getProduct().getPrice())
                                    .build();
                            cart.getItems().add(item);
                        });

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return cartRepository.findById(cart.getCartId()).orElseThrow();
    }

    @Override
    public CartBO updateItemQuantity(String accessToken, Integer cartItemId, Integer quantity) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        CartBO cart = getActiveCartOrThrow(individual);

        CartItemBO item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        assertItemBelongsToCart(item, cart);

        if (quantity <= 0) {
            cart.getItems().removeIf(i -> i.getCartItemId().equals(cartItemId));
            cartItemRepository.delete(item);
        } else {
            validateStock(item.getVariation(), quantity);
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return cartRepository.findById(cart.getCartId()).orElseThrow();
    }

    @Override
    public CartBO removeItem(String accessToken, Integer cartItemId) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        CartBO cart = getActiveCartOrThrow(individual);

        CartItemBO item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        assertItemBelongsToCart(item, cart);
        cart.getItems().removeIf(i -> i.getCartItemId().equals(cartItemId));
        cartItemRepository.delete(item);
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return cartRepository.findById(cart.getCartId()).orElseThrow();
    }

    @Override
    public CartBO clearCart(String accessToken) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        CartBO cart = getActiveCartOrThrow(individual);
        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    public CartBO checkoutCart(PrivateIndividualBO individual) {
        CartBO cart = getActiveCartOrThrow(individual);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio.");
        }
        cart.setStatus(CartStatusBO.CHECKED_OUT);
        cart.setUpdatedAt(Instant.now());
        return cartRepository.save(cart);
    }

    public CartBO findOrCreateActiveCart(PrivateIndividualBO individual) {
        return cartRepository
                .findByIndividualIdAndStatus(individual.getIndividualId(), CartStatusBO.ACTIVE)
                .orElseGet(() -> cartRepository.save(CartBO.builder()
                        .individualId(individual.getIndividualId())
                        .build()));
    }

    private CartBO getActiveCartOrThrow(PrivateIndividualBO individual) {
        return cartRepository
                .findByIndividualIdAndStatus(individual.getIndividualId(), CartStatusBO.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Nenhum carrinho ativo encontrado."));
    }

    private void validateStock(ProductVariationBO variation, int requested) {
        if (!variation.hasStock(requested)) {
            throw new IllegalArgumentException(
                    String.format("Estoque insuficiente para '%s'. Disponível: %d.",
                            variation.getSkuCode(), variation.getStock()));
        }
    }

    private void assertItemBelongsToCart(CartItemBO item, CartBO cart) {
        if (!item.getCartId().equals(cart.getCartId())) {
            throw new IllegalArgumentException("Este item não pertence ao seu carrinho.");
        }
    }
}
