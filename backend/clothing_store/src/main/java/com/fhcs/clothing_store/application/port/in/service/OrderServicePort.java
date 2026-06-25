package com.fhcs.clothing_store.application.port.in.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.cart.CheckoutRequest;

public interface OrderServicePort {
    OrderBO checkout(String accessToken, CheckoutRequest request);
    OrderBO confirmOrder(Integer orderId);
    OrderBO markAsPaid(Integer orderId);
    OrderBO markAsShipped(Integer orderId);
    OrderBO markAsDelivered(Integer orderId);
    OrderBO cancelOrder(String accessToken, Integer orderId);
    Page<OrderBO> getMyOrders(String accessToken, Pageable pageable);
    OrderBO getMyOrderById(String accessToken, Integer orderId);
}
