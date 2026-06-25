package com.fhcs.clothing_store.application.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fhcs.clothing_store.application.port.in.service.OrderServicePort;
import com.fhcs.clothing_store.application.port.in.service.PrivateIndividualServicePort;
import com.fhcs.clothing_store.application.port.out.order.OrderRepositoryPort;
import com.fhcs.clothing_store.application.port.out.product.variation.ProductVariationRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.PrivateIndividualBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartBO;
import com.fhcs.clothing_store.core.domain.bo.cart.CartItemBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderItemBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderStatusBO;
import com.fhcs.clothing_store.core.domain.bo.product.variation.ProductVariationBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CheckoutRequest;

@Service
@Transactional
public class OrderUseCase implements OrderServicePort {

    private final OrderRepositoryPort orderRepository;
    private final ProductVariationRepositoryPort variationRepository;
    private final CartUseCase cartUseCase;
    private final PrivateIndividualServicePort individualService;

    public OrderUseCase(OrderRepositoryPort orderRepository,
            ProductVariationRepositoryPort variationRepository,
            CartUseCase cartUseCase,
            PrivateIndividualServicePort individualService) {
        this.orderRepository = orderRepository;
        this.variationRepository = variationRepository;
        this.cartUseCase = cartUseCase;
        this.individualService = individualService;
    }

    @Override
    public OrderBO checkout(String accessToken, CheckoutRequest request) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        CartBO cart = cartUseCase.checkoutCart(individual);

        for (CartItemBO cartItem : cart.getItems()) {
            ProductVariationBO variation = cartItem.getVariation();
            if (!variation.hasStock(cartItem.getQuantity())) {
                throw new IllegalStateException(
                        String.format("Estoque insuficiente para '%s'. Disponível: %d.",
                                variation.getSkuCode(), variation.getStock()));
            }
        }

        OrderBO order = OrderBO.builder()
                .individualId(individual.getIndividualId())
                .paymentMethod(request.getPaymentMethod())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItemBO cartItem : cart.getItems()) {
            ProductVariationBO variation = cartItem.getVariation();
            BigDecimal subtotal =
                    cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItemBO orderItem = OrderItemBO.builder()
                    .variation(variation)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .subtotal(subtotal)
                    .build();

            order.getItems().add(orderItem);
            total = total.add(subtotal);

            variation.setStock(variation.getStock() - cartItem.getQuantity());
            variationRepository.save(variation);
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }

    @Override
    public OrderBO confirmOrder(Integer orderId) {
        return transitionStatus(orderId, OrderStatusBO.PENDING, OrderStatusBO.CONFIRMED);
    }

    @Override
    public OrderBO markAsPaid(Integer orderId) {
        return transitionStatus(orderId, OrderStatusBO.CONFIRMED, OrderStatusBO.PAID);
    }

    @Override
    public OrderBO markAsShipped(Integer orderId) {
        return transitionStatus(orderId, OrderStatusBO.PAID, OrderStatusBO.SHIPPED);
    }

    @Override
    public OrderBO markAsDelivered(Integer orderId) {
        return transitionStatus(orderId, OrderStatusBO.SHIPPED, OrderStatusBO.DELIVERED);
    }

    @Override
    public OrderBO cancelOrder(String accessToken, Integer orderId) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        OrderBO order = getOrderOrThrow(orderId);
        assertOrderBelongsToIndividual(order, individual);

        if (order.getStatus() == OrderStatusBO.SHIPPED
                || order.getStatus() == OrderStatusBO.DELIVERED) {
            throw new IllegalStateException(
                    "Pedido não pode ser cancelado no status: " + order.getStatus());
        }

        if (order.getStatus() == OrderStatusBO.CONFIRMED
                || order.getStatus() == OrderStatusBO.PAID) {
            restoreStock(order);
        }

        order.setStatus(OrderStatusBO.CANCELLED);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    @Override
    public Page<OrderBO> getMyOrders(String accessToken, Pageable pageable) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        return orderRepository.findPageByIndividualId(individual.getIndividualId(), pageable);
    }

    @Override
    public OrderBO getMyOrderById(String accessToken, Integer orderId) {
        PrivateIndividualBO individual = individualService.getPrivateIndividualByToken(accessToken);
        OrderBO order = getOrderOrThrow(orderId);
        assertOrderBelongsToIndividual(order, individual);
        return order;
    }

    private OrderBO transitionStatus(Integer orderId, OrderStatusBO expected, OrderStatusBO next) {
        OrderBO order = getOrderOrThrow(orderId);
        if (order.getStatus() != expected) {
            throw new IllegalStateException(
                    String.format("Status inválido para transição. Esperado: %s, Atual: %s",
                            expected, order.getStatus()));
        }
        order.setStatus(next);
        order.setUpdatedAt(Instant.now());
        return orderRepository.save(order);
    }

    private void restoreStock(OrderBO order) {
        for (OrderItemBO item : order.getItems()) {
            ProductVariationBO variation = item.getVariation();
            variation.setStock(variation.getStock() + item.getQuantity());
            variationRepository.save(variation);
        }
    }

    private OrderBO getOrderOrThrow(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Pedido não encontrado: " + orderId));
    }

    private void assertOrderBelongsToIndividual(OrderBO order, PrivateIndividualBO individual) {
        if (!order.getIndividualId().equals(individual.getIndividualId())) {
            throw new IllegalArgumentException("Pedido não pertence ao usuário autenticado.");
        }
    }
}
