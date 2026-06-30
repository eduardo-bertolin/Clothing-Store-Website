package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ImageFileBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageRequestBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageUpdateRequestBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.ProductImagesRequest;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.ProductImagesUpdateRequest;

public class ProductImageRequestMapper {

    public static ProductImageRequestBO toBO(ProductImagesRequest request) {
        ProductImageRequestBO bo = new ProductImageRequestBO();
        bo.setProductId(request.getProductId());
        bo.setMainImage(ImageFileBO.from(request.getMainImage()));
        List<ImageFileBO> carouselImages = request.carouselImages != null
            ? request.carouselImages.stream().map(ImageFileBO::from).toList()
            : List.of();
        bo.setCarouselImages(carouselImages);
        return bo;
    }

    public static ProductImageUpdateRequestBO toUpdateBO(Integer productId, ProductImagesUpdateRequest request) {
        ProductImageUpdateRequestBO bo = new ProductImageUpdateRequestBO();
        bo.setProductId(productId);

        bo.setNewMainImage(request.getNewMainImage() != null && !request.getNewMainImage().isEmpty()
            ? ImageFileBO.from(request.getNewMainImage())
            : null);

        bo.setNewCarouselImages(request.getNewCarouselImages() != null
            ? request.getNewCarouselImages().stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(ImageFileBO::from)
                .toList()
            : List.of());

        bo.setRemovedImageIds(request.getRemovedImageIds() != null
            ? request.getRemovedImageIds()
            : List.of());

        bo.setExistingCarouselIds(request.getExistingCarouselIds() != null
            ? request.getExistingCarouselIds()
            : List.of());

        return bo;
    }
}
