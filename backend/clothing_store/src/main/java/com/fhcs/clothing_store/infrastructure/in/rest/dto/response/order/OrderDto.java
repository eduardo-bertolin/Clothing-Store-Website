package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.Order;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderDto {

    private Integer orderId;
    private String status;
    private BigDecimal total;
    private String paymentMethod;
    private Instant createdAt;
    private List<OrderItemDto> items;

    public static OrderDto fromOrder(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(OrderItemDto::fromOrderItem)
                .toList();
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }

    public static OrderDto fromBO(OrderBO order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(OrderItemDto::fromBO)
                .toList();
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }
}