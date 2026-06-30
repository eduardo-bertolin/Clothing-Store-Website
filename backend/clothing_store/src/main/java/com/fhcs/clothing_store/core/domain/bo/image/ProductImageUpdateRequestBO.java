package com.fhcs.clothing_store.core.domain.bo.image;

import java.util.List;

public class ProductImageUpdateRequestBO {

    private Integer productId;
    private ImageFileBO newMainImage;
    private List<ImageFileBO> newCarouselImages;
    private List<Integer> removedImageIds;
    private List<Integer> existingCarouselIds;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public ImageFileBO getNewMainImage() {
        return newMainImage;
    }

    public void setNewMainImage(ImageFileBO newMainImage) {
        this.newMainImage = newMainImage;
    }

    public List<ImageFileBO> getNewCarouselImages() {
        return newCarouselImages;
    }

    public void setNewCarouselImages(List<ImageFileBO> newCarouselImages) {
        this.newCarouselImages = newCarouselImages;
    }

    public List<Integer> getRemovedImageIds() {
        return removedImageIds;
    }

    public void setRemovedImageIds(List<Integer> removedImageIds) {
        this.removedImageIds = removedImageIds;
    }

    public List<Integer> getExistingCarouselIds() {
        return existingCarouselIds;
    }

    public void setExistingCarouselIds(List<Integer> existingCarouselIds) {
        this.existingCarouselIds = existingCarouselIds;
    }
}
