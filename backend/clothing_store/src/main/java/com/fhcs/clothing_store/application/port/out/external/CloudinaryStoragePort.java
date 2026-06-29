package com.fhcs.clothing_store.application.port.out.external;

import com.fhcs.clothing_store.core.domain.bo.image.ImageFileBO;

public interface CloudinaryStoragePort {
    String uploadImage(ImageFileBO image, Integer productId);
}
