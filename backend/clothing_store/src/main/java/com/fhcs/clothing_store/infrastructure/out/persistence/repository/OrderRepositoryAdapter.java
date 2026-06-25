package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.order.OrderRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.PrivateIndividual;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.Order;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.order.OrderItem;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.product.variation.ProductVariation;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.PrivateIndividualRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.order.OrderRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.product.variation.ProductVariationRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.OrderEntityMapper;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductEntityMapper;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper mapper;
    private final PrivateIndividualRepository individualRepository;
    private final ProductVariationRepository variationRepository;
    private final ProductEntityMapper productMapper;

    public OrderRepositoryAdapter(OrderRepository orderRepository, OrderEntityMapper mapper,
            PrivateIndividualRepository individualRepository,
            ProductVariationRepository variationRepository, ProductEntityMapper productMapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.individualRepository = individualRepository;
        this.variationRepository = variationRepository;
        this.productMapper = productMapper;
    }

    @Override
    public OrderBO save(OrderBO bo) {
        Order entity;
        if (bo.getOrderId() != null) {
            entity = orderRepository.findById(bo.getOrderId()).orElse(new Order());
        } else {
            entity = new Order();
        }

        PrivateIndividual individual =
                individualRepository.findById(bo.getIndividualId()).orElseThrow();
        entity.setIndividual(individual);
        entity.setStatus(mapper.statusToEntity(bo.getStatus()));
        entity.setTotal(bo.getTotal());
        entity.setPaymentMethod(bo.getPaymentMethod());
        if (bo.getUpdatedAt() != null) entity.setUpdatedAt(bo.getUpdatedAt());

        entity.getItems().clear();
        if (bo.getItems() != null) {
            Order finalEntity = entity;
            bo.getItems().forEach(itemBO -> {
                ProductVariation variation =
                        variationRepository.findById(itemBO.getVariation().getVariationId())
                                .orElseThrow();
                OrderItem item = OrderItem.builder()
                        .orderItemId(itemBO.getOrderItemId())
                        .order(finalEntity)
                        .variation(variation)
                        .quantity(itemBO.getQuantity())
                        .unitPrice(itemBO.getUnitPrice())
                        .subtotal(itemBO.getSubtotal())
                        .build();
                finalEntity.getItems().add(item);
            });
        }

        return mapper.orderToBO(orderRepository.save(entity));
    }

    @Override
    public Optional<OrderBO> findById(Integer orderId) {
        return orderRepository.findById(orderId).map(mapper::orderToBO);
    }

    @Override
    public Page<OrderBO> findPageByIndividualId(Integer individualId, Pageable pageable) {
        return orderRepository.findByIndividual_IndividualId(individualId, pageable)
                .map(mapper::orderToBO);
    }
}
