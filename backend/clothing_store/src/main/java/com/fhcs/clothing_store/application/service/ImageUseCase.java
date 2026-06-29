package com.fhcs.clothing_store.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.fhcs.clothing_store.application.port.in.service.ImageServicePort;
import com.fhcs.clothing_store.application.port.in.service.admin.AdminProductServicePort;
import com.fhcs.clothing_store.application.port.out.external.CloudinaryStoragePort;
import com.fhcs.clothing_store.application.port.out.persistence.ImageRepositoryPort;
import com.fhcs.clothing_store.core.domain.bo.image.ImageType;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageRequestBO;
import com.fhcs.clothing_store.core.domain.bo.product.ProductBO;

@Service
public class ImageUseCase implements ImageServicePort {

    private final CloudinaryStoragePort storagePort;
    private final AdminProductServicePort productServicePort;
    private final ImageRepositoryPort imageRepositoryPort;

    public ImageUseCase(CloudinaryStoragePort storagePort, AdminProductServicePort productServicePort,
            ImageRepositoryPort imageRepositoryPort) {
        this.storagePort = storagePort;
        this.productServicePort = productServicePort;
        this.imageRepositoryPort = imageRepositoryPort;
    }

    @Override
    public List<ProductImageBO> uploadProductImages(ProductImageRequestBO request) {
        ProductBO product = productServicePort.getProductById(request.getProductId());

        String mainImageUrl = storagePort.uploadImage(request.getMainImage(), product.getProductId());

        List<String> carouselImagesUrls = request.getCarouselImages().stream()
                .map(carouselImage -> storagePort.uploadImage(carouselImage, request.getProductId()))
                .toList();

        List<ProductImageBO> saved = imageRepositoryPort
                .saveAll(buildImageBOs(product, mainImageUrl, carouselImagesUrls));

        return saved;

    }

    private List<ProductImageBO> buildImageBOs(ProductBO product, String mainImageUrl,
            List<String> carouselImagesUrls) {
        List<ProductImageBO> imageBOs = new ArrayList<>();

        imageBOs.add(ProductImageBO.DRAFT(product, mainImageUrl, ImageType.MAIN, 0));
        IntStream.range(0, carouselImagesUrls.size()).forEach(i -> {
            imageBOs.add(ProductImageBO.DRAFT(product, carouselImagesUrls.get(i), ImageType.CAROUSEL, i + 1));
        });
        return imageBOs;
    }

}
