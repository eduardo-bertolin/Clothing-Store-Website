package com.fhcs.clothing_store.infrastructure.out.persistence.entity.order;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}