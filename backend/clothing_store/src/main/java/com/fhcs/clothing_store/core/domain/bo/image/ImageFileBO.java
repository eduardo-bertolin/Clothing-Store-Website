package com.fhcs.clothing_store.core.domain.bo.image;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.fhcs.clothing_store.core.domain.exception.FileProcessingException;

public class ImageFileBO {
    private final byte[] content;
    private final String originalFilename;
    private final String contentType;

    public ImageFileBO(byte[] bytes, String originalFilename, String contentType) {
        this.content = bytes;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    public static ImageFileBO from(MultipartFile file) {
        try {
            return new ImageFileBO(
                file.getBytes(),
                file.getOriginalFilename(),
                file.getContentType()
            );
        } catch (IOException e) {
            throw new FileProcessingException("Falha ao processar o upload da imagem" , e);
        }
    }

    public byte[] getContent() {
        return content;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }
}