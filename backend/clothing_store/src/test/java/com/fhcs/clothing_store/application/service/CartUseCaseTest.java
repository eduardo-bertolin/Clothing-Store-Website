package com.fhcs.clothing_store.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.application.port.out.persistence.cart.CartItemRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.cart.CartRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartStatusBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CartItemRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartUseCaseTest {

    @Mock CartRepositoryPort cartRepository;
    @Mock CartItemRepositoryPort cartItemRepository;
    @Mock ProductVariationRepositoryPort variationRepository;
    @Mock PrivateIndividualServicePort individualService;

    @InjectMocks CartUseCase cartUseCase;

    private PrivateIndividualBO individual;
    private ProductVariationBO variation;
    private CartBO activeCart;

    @BeforeEach
    void setUp() {
        individual = new PrivateIndividualBO();
        individual.setIndividualId(1);

        ProductBO product = new ProductBO();
        product.setPrice(new BigDecimal("99.90"));

        variation = new ProductVariationBO();
        variation.setVariationId(10);
        variation.setSkuCode("SKU-001");
        variation.setStock(5);
        variation.setProduct(product);

        activeCart = CartBO.builder()
                .cartId(100)
                .individualId(1)
                .build();
    }

    // ── getOrCreateCart ────────────────────────────────────────────────────────

    @Test
    void getOrCreateCart_createsNewCartWhenNoneExists() {
        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartBO.class))).thenReturn(activeCart);

        CartBO result = cartUseCase.getOrCreateCart("token");

        assertThat(result).isEqualTo(activeCart);
        verify(cartRepository).save(any(CartBO.class));
    }

    @Test
    void getOrCreateCart_returnsExistingActiveCart() {
        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));

        CartBO result = cartUseCase.getOrCreateCart("token");

        assertThat(result).isEqualTo(activeCart);
        verify(cartRepository, never()).save(any());
    }

    // ── addItem ────────────────────────────────────────────────────────────────

    @Test
    void addItem_addsNewItemToCart() {
        CartItemRequest request = mock(CartItemRequest.class);
        when(request.getVariationId()).thenReturn(10);
        when(request.getQuantity()).thenReturn(2);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(variationRepository.findById(10)).thenReturn(Optional.of(variation));
        when(cartItemRepository.findByCartIdAndVariationId(100, 10)).thenReturn(Optional.empty());
        when(cartRepository.save(activeCart)).thenReturn(activeCart);
        when(cartRepository.findById(100)).thenReturn(Optional.of(activeCart));

        CartBO result = cartUseCase.addItem("token", request);

        assertThat(result).isEqualTo(activeCart);
        assertThat(activeCart.getItems()).hasSize(1);
    }

    @Test
    void addItem_incrementsQuantityForExistingItem() {
        CartItemRequest request = mock(CartItemRequest.class);
        when(request.getVariationId()).thenReturn(10);
        when(request.getQuantity()).thenReturn(1);

        CartItemBO existingItem = CartItemBO.builder()
                .cartItemId(1).cartId(100).variation(variation).quantity(2).unitPrice(new BigDecimal("99.90")).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(variationRepository.findById(10)).thenReturn(Optional.of(variation));
        when(cartItemRepository.findByCartIdAndVariationId(100, 10)).thenReturn(Optional.of(existingItem));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);
        when(cartRepository.findById(100)).thenReturn(Optional.of(activeCart));

        cartUseCase.addItem("token", request);

        assertThat(existingItem.getQuantity()).isEqualTo(3);
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addItem_throwsWhenVariationNotFound() {
        CartItemRequest request = mock(CartItemRequest.class);
        when(request.getVariationId()).thenReturn(99);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(variationRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.addItem("token", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Variação não encontrada");
    }

    @Test
    void addItem_throwsWhenStockInsufficient() {
        CartItemRequest request = mock(CartItemRequest.class);
        when(request.getVariationId()).thenReturn(10);
        when(request.getQuantity()).thenReturn(10);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(variationRepository.findById(10)).thenReturn(Optional.of(variation));

        assertThatThrownBy(() -> cartUseCase.addItem("token", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    // ── updateItemQuantity ─────────────────────────────────────────────────────

    @Test
    void updateItemQuantity_updatesQuantity() {
        CartItemBO item = CartItemBO.builder()
                .cartItemId(1).cartId(100).variation(variation).quantity(1).unitPrice(new BigDecimal("99.90")).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);
        when(cartRepository.findById(100)).thenReturn(Optional.of(activeCart));

        cartUseCase.updateItemQuantity("token", 1, 3);

        assertThat(item.getQuantity()).isEqualTo(3);
        verify(cartItemRepository).save(item);
    }

    @Test
    void updateItemQuantity_removesItemWhenQuantityIsZeroOrNegative() {
        CartItemBO item = CartItemBO.builder()
                .cartItemId(1).cartId(100).variation(variation).quantity(2).unitPrice(new BigDecimal("99.90")).build();
        activeCart.getItems().add(item);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);
        when(cartRepository.findById(100)).thenReturn(Optional.of(activeCart));

        cartUseCase.updateItemQuantity("token", 1, 0);

        assertThat(activeCart.getItems()).isEmpty();
        verify(cartItemRepository).delete(item);
    }

    @Test
    void updateItemQuantity_throwsWhenNoActiveCart() {
        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartUseCase.updateItemQuantity("token", 1, 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Nenhum carrinho ativo");
    }

    @Test
    void updateItemQuantity_throwsWhenItemBelongsToDifferentCart() {
        CartItemBO item = CartItemBO.builder()
                .cartItemId(1).cartId(999).variation(variation).quantity(1).unitPrice(new BigDecimal("99.90")).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartUseCase.updateItemQuantity("token", 1, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao seu carrinho");
    }

    // ── removeItem ─────────────────────────────────────────────────────────────

    @Test
    void removeItem_removesItemFromCart() {
        CartItemBO item = CartItemBO.builder()
                .cartItemId(1).cartId(100).variation(variation).quantity(2).unitPrice(new BigDecimal("99.90")).build();
        activeCart.getItems().add(item);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(item));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);
        when(cartRepository.findById(100)).thenReturn(Optional.of(activeCart));

        cartUseCase.removeItem("token", 1);

        verify(cartItemRepository).delete(item);
    }

    @Test
    void removeItem_throwsWhenItemBelongsToDifferentCart() {
        CartItemBO item = CartItemBO.builder()
                .cartItemId(1).cartId(999).variation(variation).quantity(1).unitPrice(new BigDecimal("99.90")).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> cartUseCase.removeItem("token", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao seu carrinho");
    }

    // ── clearCart ──────────────────────────────────────────────────────────────

    @Test
    void clearCart_clearsAllItems() {
        activeCart.getItems().addAll(List.of(
                CartItemBO.builder().cartItemId(1).cartId(100).build(),
                CartItemBO.builder().cartItemId(2).cartId(100).build()));

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);

        cartUseCase.clearCart("token");

        assertThat(activeCart.getItems()).isEmpty();
    }

    // ── checkoutCart ───────────────────────────────────────────────────────────

    @Test
    void checkoutCart_marksCartAsCheckedOut() {
        activeCart.getItems().add(CartItemBO.builder().cartItemId(1).cartId(100).build());

        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));
        when(cartRepository.save(activeCart)).thenReturn(activeCart);

        CartBO result = cartUseCase.checkoutCart(individual);

        assertThat(result.getStatus()).isEqualTo(CartStatusBO.CHECKED_OUT);
    }

    @Test
    void checkoutCart_throwsWhenCartIsEmpty() {
        when(cartRepository.findByIndividualIdAndStatus(1, CartStatusBO.ACTIVE)).thenReturn(Optional.of(activeCart));

        assertThatThrownBy(() -> cartUseCase.checkoutCart(individual))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("carrinho está vazio");
    }
}
