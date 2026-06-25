package com.fhcs.clothing_store.core.domain.bo.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBO {
    private Integer orderId;
    private Integer individualId;
    @Builder.Default
    private OrderStatusBO status = OrderStatusBO.PENDING;
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;
    private String paymentMethod;
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();
    @Builder.Default
    private List<OrderItemBO> items = new ArrayList<>();
}
