package com.fhcs.clothing_store.infrastructure.in.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fhcs.clothing_store.application.port.in.service.OrderServicePort;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderDto;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.response.order.OrderResponse;
import com.fhcs.clothing_store.infrastructure.in.rest.mapper.OrderDtoMapper;

@RestController
@RequestMapping("/api/orders")
public class OrderControllerAdapter {

    private final OrderServicePort orderService;
    private final OrderDtoMapper orderMapper;

    public OrderControllerAdapter(OrderServicePort orderService, OrderDtoMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping
    public PagedModel<OrderDto> getMyOrders(@RequestHeader("Authorization") String token,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OrderBO> orders = orderService.getMyOrders(token.substring(7), pageable);
        return new PagedModel<>(orders.map(orderMapper::toOrderDto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrderById(
            @RequestHeader("Authorization") String token, @PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.getMyOrderById(token.substring(7), orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido recuperado com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError("Erro ao recuperar pedido: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(
            @RequestHeader("Authorization") String token, @PathVariable Integer orderId) {
        try {
            OrderBO order = orderService.cancelOrder(token.substring(7), orderId);
            return ResponseEntity.ok(orderMapper.toOrderResponse(order, "Pedido cancelado."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(orderMapper.toOrderError("Erro ao cancelar pedido: " + e.getMessage()));
        }
    }
}
