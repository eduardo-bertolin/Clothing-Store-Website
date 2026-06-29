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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.application.port.out.persistence.order.OrderRepositoryPort;
import com.fhcs.clothing_store.application.port.out.persistence.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderItemBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderStatusBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CheckoutRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderUseCaseTest {

    @Mock OrderRepositoryPort orderRepository;
    @Mock ProductVariationRepositoryPort variationRepository;
    @Mock CartUseCase cartUseCase;
    @Mock PrivateIndividualServicePort individualService;

    @InjectMocks OrderUseCase orderUseCase;

    private PrivateIndividualBO individual;
    private ProductVariationBO variation;

    @BeforeEach
    void setUp() {
        individual = new PrivateIndividualBO();
        individual.setIndividualId(1);

        ProductBO product = new ProductBO();
        product.setPrice(new BigDecimal("150.00"));

        variation = new ProductVariationBO();
        variation.setVariationId(10);
        variation.setSkuCode("SKU-001");
        variation.setStock(10);
        variation.setProduct(product);
    }

    // ── checkout ───────────────────────────────────────────────────────────────

    @Test
    void checkout_createsOrderFromCartAndCalculatesTotal() {
        CheckoutRequest request = mock(CheckoutRequest.class);
        when(request.getPaymentMethod()).thenReturn("PIX");

        CartItemBO cartItem = CartItemBO.builder()
                .variation(variation).quantity(2).unitPrice(new BigDecimal("150.00")).build();
        CartBO cart = CartBO.builder().individualId(1).build();
        cart.getItems().add(cartItem);

        OrderBO savedOrder = OrderBO.builder().orderId(1).individualId(1).total(new BigDecimal("300.00")).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartUseCase.checkoutCart(individual)).thenReturn(cart);
        when(orderRepository.save(any(OrderBO.class))).thenReturn(savedOrder);

        OrderBO result = orderUseCase.checkout("token", request);

        assertThat(result.getTotal()).isEqualByComparingTo("300.00");
        assertThat(variation.getStock()).isEqualTo(8);
        verify(variationRepository).save(variation);
    }

    @Test
    void checkout_throwsWhenStockIsInsufficient() {
        CheckoutRequest request = mock(CheckoutRequest.class);

        variation.setStock(1);
        CartItemBO cartItem = CartItemBO.builder()
                .variation(variation).quantity(5).unitPrice(new BigDecimal("150.00")).build();
        CartBO cart = CartBO.builder().individualId(1).build();
        cart.getItems().add(cartItem);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(cartUseCase.checkoutCart(individual)).thenReturn(cart);

        assertThatThrownBy(() -> orderUseCase.checkout("token", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    // ── status transitions ─────────────────────────────────────────────────────

    @Test
    void confirmOrder_transitionsPendingToConfirmed() {
        OrderBO order = OrderBO.builder().orderId(1).status(OrderStatusBO.PENDING).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderBO result = orderUseCase.confirmOrder(1);

        assertThat(result.getStatus()).isEqualTo(OrderStatusBO.CONFIRMED);
    }

    @Test
    void confirmOrder_throwsWhenStatusIsNotPending() {
        OrderBO order = OrderBO.builder().orderId(1).status(OrderStatusBO.CANCELLED).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.confirmOrder(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Status inválido");
    }

    @Test
    void markAsPaid_transitionsConfirmedToPaid() {
        OrderBO order = OrderBO.builder().orderId(1).status(OrderStatusBO.CONFIRMED).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderBO result = orderUseCase.markAsPaid(1);

        assertThat(result.getStatus()).isEqualTo(OrderStatusBO.PAID);
    }

    @Test
    void markAsShipped_transitionsPaidToShipped() {
        OrderBO order = OrderBO.builder().orderId(1).status(OrderStatusBO.PAID).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderBO result = orderUseCase.markAsShipped(1);

        assertThat(result.getStatus()).isEqualTo(OrderStatusBO.SHIPPED);
    }

    @Test
    void markAsDelivered_transitionsShippedToDelivered() {
        OrderBO order = OrderBO.builder().orderId(1).status(OrderStatusBO.SHIPPED).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderBO result = orderUseCase.markAsDelivered(1);

        assertThat(result.getStatus()).isEqualTo(OrderStatusBO.DELIVERED);
    }

    // ── cancelOrder ────────────────────────────────────────────────────────────

    @Test
    void cancelOrder_cancelsPendingOrderWithoutRestoringStock() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).status(OrderStatusBO.PENDING).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        OrderBO result = orderUseCase.cancelOrder("token", 1);

        assertThat(result.getStatus()).isEqualTo(OrderStatusBO.CANCELLED);
        verify(variationRepository, never()).save(any());
    }

    @Test
    void cancelOrder_cancelsConfirmedOrderAndRestoresStock() {
        OrderItemBO orderItem = OrderItemBO.builder().variation(variation).quantity(3).build();
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).status(OrderStatusBO.CONFIRMED).build();
        order.getItems().add(orderItem);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderUseCase.cancelOrder("token", 1);

        assertThat(variation.getStock()).isEqualTo(13);
        verify(variationRepository).save(variation);
    }

    @Test
    void cancelOrder_cancelsPaidOrderAndRestoresStock() {
        variation.setStock(0);
        OrderItemBO orderItem = OrderItemBO.builder().variation(variation).quantity(2).build();
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).status(OrderStatusBO.PAID).build();
        order.getItems().add(orderItem);

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderUseCase.cancelOrder("token", 1);

        assertThat(variation.getStock()).isEqualTo(2);
        verify(variationRepository).save(variation);
    }

    @Test
    void cancelOrder_throwsWhenOrderIsShipped() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).status(OrderStatusBO.SHIPPED).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.cancelOrder("token", 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não pode ser cancelado");
    }

    @Test
    void cancelOrder_throwsWhenOrderIsDelivered() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).status(OrderStatusBO.DELIVERED).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.cancelOrder("token", 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não pode ser cancelado");
    }

    @Test
    void cancelOrder_throwsWhenOrderBelongsToDifferentUser() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(99).status(OrderStatusBO.PENDING).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.cancelOrder("token", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao usuário");
    }

    // ── getMyOrders ────────────────────────────────────────────────────────────

    @Test
    void getMyOrders_returnsPaginatedOrdersForUser() {
        Pageable pageable = Pageable.unpaged();
        Page<OrderBO> page = new PageImpl<>(List.of(OrderBO.builder().orderId(1).individualId(1).build()));

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findPageByIndividualId(1, pageable)).thenReturn(page);

        Page<OrderBO> result = orderUseCase.getMyOrders("token", pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    // ── getMyOrderById ─────────────────────────────────────────────────────────

    @Test
    void getMyOrderById_returnsOrderForCorrectUser() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(1).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        OrderBO result = orderUseCase.getMyOrderById("token", 1);

        assertThat(result.getOrderId()).isEqualTo(1);
    }

    @Test
    void getMyOrderById_throwsWhenOrderNotFound() {
        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderUseCase.getMyOrderById("token", 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    void getMyOrderById_throwsWhenOrderBelongsToDifferentUser() {
        OrderBO order = OrderBO.builder().orderId(1).individualId(99).build();

        when(individualService.getPrivateIndividualByToken("token")).thenReturn(individual);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderUseCase.getMyOrderById("token", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pertence ao usuário");
    }
}
