package com.fhcs.clothing_store.infrastructure.in.rest.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesRequest {
    
    @NotNull
    public Integer productId;
    public MultipartFile mainImage;
    public List<MultipartFile> carouselImages;
}
