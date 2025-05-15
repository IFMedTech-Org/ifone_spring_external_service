package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final String UPLOAD_DIR = "uploads";

    public String saveImage(String base64, String extension) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        String filename = "prescription_" + UUID.randomUUID() + "." + extension;

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            Files.createDirectories(uploadDir.toPath());
        }

        File file = new File(uploadDir, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decodedBytes);
        }

        return file.getAbsolutePath(); // For prod, return S3 URL instead
    }

    public String getExtensionFromMime(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/bmp" -> "bmp";
            case "image/tiff" -> "tiff";
            default -> throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
        };
    }
}
