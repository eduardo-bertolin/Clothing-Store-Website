package com.fhcs.clothing_store.core.domain.bo.image;

import java.util.List;

public class ProductImageRequestBO {
    public Integer productId;
    public ImageFileBO mainImage;
    public List<ImageFileBO> carouselImages;

    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public ImageFileBO getMainImage() {
        return mainImage;
    }
    public void setMainImage(ImageFileBO mainImage) {
        this.mainImage = mainImage;
    }
    public List<ImageFileBO> getCarouselImages() {
        return carouselImages;
    }
    public void setCarouselImages(List<ImageFileBO> carouselImages) {
        this.carouselImages = carouselImages;
    }
}
