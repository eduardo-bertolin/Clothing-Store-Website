package com.fhcs.clothing_store.application.port.out.order;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fhcs.clothing_store.core.domain.bo.order.OrderBO;

public interface OrderRepositoryPort {
    OrderBO save(OrderBO order);
    Optional<OrderBO> findById(Integer orderId);
    Page<OrderBO> findPageByIndividualId(Integer individualId, Pageable pageable);
}
