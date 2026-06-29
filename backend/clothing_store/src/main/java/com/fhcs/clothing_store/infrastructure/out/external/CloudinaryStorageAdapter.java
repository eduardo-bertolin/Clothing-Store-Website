package com.fhcs.clothing_store.infrastructure.out.external;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fhcs.clothing_store.application.port.out.external.CloudinaryStoragePort;
import com.fhcs.clothing_store.core.domain.bo.image.ImageFileBO;
import com.fhcs.clothing_store.core.domain.exception.StorageException;

@Component
public class CloudinaryStorageAdapter implements CloudinaryStoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(ImageFileBO image, Integer productId) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                            image.getContent(),
                                ObjectUtils.asMap(
                                        "folder", "products/" + productId,
                                        "resource_type", "image"));

            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new StorageException("Falha ao realizar o upload da imagem na Cloudinary", e);
        }
    }

}
