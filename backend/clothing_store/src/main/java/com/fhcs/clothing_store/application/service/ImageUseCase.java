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
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageUpdateRequestBO;
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

    @Override
    public List<ProductImageBO> updateProductImages(ProductImageUpdateRequestBO request) {
        ProductBO product = productServicePort.getProductById(request.getProductId());

        deleteRemovedImages(request.getRemovedImageIds());
        reorderExistingCarouselImages(request.getExistingCarouselIds());
        replaceMainImageIfNeeded(product, request);
        uploadNewCarouselImages(product, request);

        return imageRepositoryPort.findByProductId(request.getProductId());
    }

    private void deleteRemovedImages(List<Integer> removedImageIds) {
        if (removedImageIds == null || removedImageIds.isEmpty()) return;
        removedImageIds.forEach(imageRepositoryPort::deleteById);
    }

    private void reorderExistingCarouselImages(List<Integer> existingCarouselIds) {
        if (existingCarouselIds == null || existingCarouselIds.isEmpty()) return;
        IntStream.range(0, existingCarouselIds.size()).forEach(i -> {
            Integer imageId = existingCarouselIds.get(i);
            imageRepositoryPort.findById(imageId).ifPresent(image -> {
                image.setPosition(i + 1);
                imageRepositoryPort.save(image);
            });
        });
    }

    private void replaceMainImageIfNeeded(ProductBO product, ProductImageUpdateRequestBO request) {
        if (request.getNewMainImage() == null) return;

        imageRepositoryPort.findByProductId(request.getProductId()).stream()
                .filter(img -> ImageType.MAIN.equals(img.getType()))
                .findFirst()
                .ifPresent(existing -> imageRepositoryPort.deleteById(existing.getId()));

        String newUrl = storagePort.uploadImage(request.getNewMainImage(), product.getProductId());
        imageRepositoryPort.save(ProductImageBO.DRAFT(product, newUrl, ImageType.MAIN, 0));
    }

    private void uploadNewCarouselImages(ProductBO product, ProductImageUpdateRequestBO request) {
        if (request.getNewCarouselImages() == null || request.getNewCarouselImages().isEmpty()) return;

        int startPosition = request.getExistingCarouselIds() != null
                ? request.getExistingCarouselIds().size() + 1
                : 1;

        for (int i = 0; i < request.getNewCarouselImages().size(); i++) {
            String url = storagePort.uploadImage(request.getNewCarouselImages().get(i), product.getProductId());
            imageRepositoryPort.save(ProductImageBO.DRAFT(product, url, ImageType.CAROUSEL, startPosition + i));
        }
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
