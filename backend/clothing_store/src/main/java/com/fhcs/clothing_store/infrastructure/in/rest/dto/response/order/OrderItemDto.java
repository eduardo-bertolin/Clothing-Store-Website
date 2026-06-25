package com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order;

import java.math.BigDecimal;

import com.fhcs.clothing_store.core.domain.bo.order.OrderItemBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.OrderItem;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderItemDto {

    private Integer orderItemId;
    private Integer variationId;
    private String productName;
    private String color;
    private String size;
    private String skuCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public static OrderItemDto fromOrderItem(OrderItem item) {
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

    public static OrderItemDto fromBO(OrderItemBO item) {
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
}