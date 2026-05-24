package com.slotify.backend.spring.s3;

import com.slotify.backend.spring.exceptions.InvalidImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ImageFormatValidator {

    private static final List<String> ALLOWED_IMAGES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    public boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGES.contains(contentType.toLowerCase())) {
            throw new InvalidImageException("El archivo debe ser una imagen válida (JPEG, PNG, GIF o WEBP)");
        }

        long maxSizeBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidImageException("La imagen no puede superar los 5MB");
        }

        return true;
    }
}
