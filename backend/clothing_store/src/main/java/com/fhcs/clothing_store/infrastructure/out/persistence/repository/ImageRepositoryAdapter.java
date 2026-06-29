package com.fhcs.clothing_store.infrastructure.out.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fhcs.clothing_store.application.port.out.persistence.ImageRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;
import com.fhcs.clothing_store.infrastructure.out.persistence.entity.ProductImage;
import com.fhcs.clothing_store.infrastructure.out.persistence.jpa.ImageRepository;
import com.fhcs.clothing_store.infrastructure.out.persistence.mapper.ProductImageEntityMapper;

@Component
public class ImageRepositoryAdapter implements ImageRepositoryPort {

    private final ImageRepository imageRepository;
    private final ProductImageEntityMapper mapper;

    public ImageRepositoryAdapter(ImageRepository imageRepository, ProductImageEntityMapper mapper) {
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductImageBO> saveAll(List<ProductImageBO> images) {

        List<ProductImage> productImages = images.stream()
            .map(image -> mapper.toEntity(image))
            .toList();
        
        List<ProductImage> productImagesSaved = imageRepository.saveAll(productImages);

        return productImagesSaved.stream()
            .map(image -> {
                return mapper.toBO(image);
            }).toList();
    }

    @Override
    public List<ProductImageBO> findByProductId(Integer productId) {
        return imageRepository.findByProductProductId(productId)
                .stream()
                .map(mapper::toBO)
                .toList();
    }

    @Override
    public List<ProductImageBO> findByProductIds(List<Integer> productIds) {
        return imageRepository.findByProductProductIdIn(productIds)
                .stream()
                .map(mapper::toBO)
                .toList();
    }

    @Override
    public void deleteById(Integer id) {
        imageRepository.deleteById(id);
    }
    
}
