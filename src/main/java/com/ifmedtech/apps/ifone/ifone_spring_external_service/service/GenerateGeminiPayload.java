package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerateGeminiPayload {

    public String convertToGeminiJson(String base64File, String prompt) throws Exception {
        if (base64File == null || base64File.isBlank()) {
            throw new IllegalArgumentException("Base64 input is missing");
        }

        String mimeType = detectMimeType(base64File);

        Map<String, Object> part1 = Map.of("text", prompt);

        Map<String, Object> part2 = Map.of(
                "inline_data", Map.of(
                        "mime_type", mimeType,
                        "data", base64File
                )
        );

        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(part1, part2));

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contents", List.of(contents));

        return new ObjectMapper().writeValueAsString(requestMap);
    }

    public String detectMimeType(String base64Data) {
        if (base64Data.startsWith("/9j/") || base64Data.startsWith("ffd8")) {
            return "image/jpeg";  // JPEG or JPG
        } else if (base64Data.startsWith("iVBOR")) {
            return "image/png";   // PNG
        } else if (base64Data.startsWith("R0lGOD")) {
            return "image/gif";   // GIF
        } else if (base64Data.startsWith("Qk")) {
            return "image/bmp";   // BMP
        } else if (base64Data.startsWith("SUkq")) {
            return "image/tiff";  // TIFF
        } else {
            throw new IllegalArgumentException("Unsupported image type");
        }
    }

}
