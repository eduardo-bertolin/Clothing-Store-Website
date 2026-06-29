package com.fhcs.clothing_store.infrastructure.in.rest.mapper;

import java.util.List;

import com.fhcs.clothing_store.core.domain.bo.image.ImageFileBO;
import com.fhcs.clothing_store.core.domain.bo.image.ProductImageRequestBO;
import com.fhcs.clothing_store.infrastructure.in.rest.dto.request.ProductImagesRequest;

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
}
