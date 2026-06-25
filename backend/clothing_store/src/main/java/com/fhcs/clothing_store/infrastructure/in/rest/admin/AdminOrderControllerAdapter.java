package com.fhcs.clothing_store.infrastructure.in.rest.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.OrderServicePort;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.OrderDtoMapper;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminOrderControllerAdapter {

    private final OrderServicePort orderService;
    private final OrderDtoMapper orderMapper;

    public AdminOrderControllerAdapter(OrderServicePort orderService, OrderDtoMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirm(@PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido confirmado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> markAsPaid(@PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.markAsPaid(orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido marcado como pago."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> markAsShipped(@PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.markAsShipped(orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido enviado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError(e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> markAsDelivered(@PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.markAsDelivered(orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido entregue."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError(e.getMessage()));
        }
    }
}
