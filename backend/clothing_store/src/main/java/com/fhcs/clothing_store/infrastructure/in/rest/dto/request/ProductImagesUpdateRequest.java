package com.fhcs.clothing_store.infrastructure.in.rest.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesUpdateRequest {

    private MultipartFile newMainImage;
    private List<MultipartFile> newCarouselImages;
    private List<Integer> removedImageIds;
    private List<Integer> existingCarouselIds;
}
