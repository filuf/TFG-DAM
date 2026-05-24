package com.slotify.backend.spring.s3;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;
    private static final String S3_BUCKET_NAME = "slotify-tfg-s3";


    public String generateFileName(MultipartFile file) {
        if (file == null) return null;

        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
    public void uploadFile(String fileKey, MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(S3_BUCKET_NAME, fileKey, inputStream);
        }
    }

    public String getTemporalUrl(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) return null;
        Duration tiempoExpiracion = Duration.ofMinutes(15);

        URL signedUrl = s3Template.createSignedGetURL(S3_BUCKET_NAME, fileKey, tiempoExpiracion);

        return signedUrl.toString();
    }

    public void deleteFile(String fileKey) {
        s3Template.deleteObject(fileKey);
    }

}
