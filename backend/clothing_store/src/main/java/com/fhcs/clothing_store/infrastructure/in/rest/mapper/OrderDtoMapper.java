package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.core.domain.bo.order.OrderItemBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderItemDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderResponse;

@Component
public class OrderDtoMapper {

    public OrderItemDto toOrderItemDto(OrderItemBO item) {
        return OrderItemDto.builder()
                .orderItemId(item.getOrderItemId())
                .variationId(item.getVariation().getVariationId())
                .productName(item.getVariation().getProduct().getName())
                .color(item.getVariation().getColor().getColor())
                .size(item.getVariation().getSize().getSize())
                .skuCode(item.getVariation().getSkuCode())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public OrderDto toOrderDto(OrderBO order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(this::toOrderItemDto)
                .toList();
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    public OrderResponse toOrderResponse(OrderBO order, String message) {
        return OrderResponse.builder()
                .success(true)
                .message(message)
                .order(toOrderDto(order))
                .build();
    }

    public OrderResponse toOrderListResponse(List<OrderBO> orders, String message) {
        List<OrderDto> dtos = orders.stream().map(this::toOrderDto).toList();
        return OrderResponse.builder()
                .success(true)
                .message(message)
                .orders(dtos)
                .build();
    }

    public OrderResponse toOrderError(String message) {
        return OrderResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
