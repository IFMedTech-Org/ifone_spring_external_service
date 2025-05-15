package com.ifmedtech.apps.ifone.ifone_spring_external_service.service.external.storage;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.util.EnvironmentUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Base64;

@Service
public class ImageStorageService {

    @Value("${app.download.prescription_uploads}")
    private String prescriptionUploadPath;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.s3.bucket.region}")
    private String awsRegion;

    private final EnvironmentUtil environmentUtil;

    private S3Client s3Client;

    public ImageStorageService(EnvironmentUtil environmentUtil) {
        this.environmentUtil = environmentUtil;
    }

    private void initializeS3Client() {
        if (s3Client == null) {
            s3Client = S3Client.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    public String uploadFile(String base64, String folderName, String extension) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            String fileName = "prescription";

            String filePath = folderName + fileName + "-" + Instant.now().toEpochMilli() + extension;

            if (environmentUtil.isLocal()) {
                return saveLocally(decodedBytes, filePath);
            } else {
                initializeS3Client();
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePath)
                        .contentType(getContentTypeFromExtension(extension))
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(decodedBytes));
                return filePath; // Or return full S3 URL if needed
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private String saveLocally(byte[] fileBytes, String filePath) throws IOException {
        // filePath is like "record/image/prescription-1684157712345.jpg"
        File fullPath = new File(prescriptionUploadPath, filePath);

        File parentDir = fullPath.getParentFile();
        if (!parentDir.exists()) {
            Files.createDirectories(parentDir.toPath());
        }

        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(fileBytes);
        }

        return fullPath.getCanonicalPath();
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

    private String getContentTypeFromExtension(String ext) {
        return switch (ext.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "tiff" -> "image/tiff";
            default -> "application/octet-stream";
        };
    }
}
