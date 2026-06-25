package com.fhcs.clothing_store.application.port.out.product;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;

public interface ProductRepositoryPort {
    ProductBO save(ProductBO product);
    Optional<ProductBO> findById(Integer id);
    Page<ProductBO> findAll(Pageable pageable);
    void delete(ProductBO product);
}
