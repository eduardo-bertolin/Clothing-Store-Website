package com.fhcs.clothing_store.infrastructure.out.persistence.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderItemBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderStatusBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.Order;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.OrderItem;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.OrderStatus;

@Component
public class OrderEntityMapper {

    private final ProductEntityMapper productMapper;

    public OrderEntityMapper(ProductEntityMapper productMapper) {
        this.productMapper = productMapper;
    }

    public OrderStatusBO statusToBO(OrderStatus status) {
        return OrderStatusBO.valueOf(status.name());
    }

    public OrderStatus statusToEntity(OrderStatusBO status) {
        return OrderStatus.valueOf(status.name());
    }

    public OrderItemBO orderItemToBO(OrderItem entity) {
        if (entity == null) return null;
        return OrderItemBO.builder()
                .orderItemId(entity.getOrderItemId())
                .variation(productMapper.variationToBO(entity.getVariation()))
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .build();
    }

    public OrderBO orderToBO(Order entity) {
        if (entity == null) return null;
        return OrderBO.builder()
                .orderId(entity.getOrderId())
                .individualId(entity.getIndividual().getIndividualId())
                .status(statusToBO(entity.getStatus()))
                .total(entity.getTotal())
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(entity.getItems().stream()
                        .map(this::orderItemToBO)
                        .collect(Collectors.toList()))
                .build();
    }
}
