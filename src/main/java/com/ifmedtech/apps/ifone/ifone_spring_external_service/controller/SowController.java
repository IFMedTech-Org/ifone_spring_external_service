package com.ifmedtech.apps.ifone.ifone_spring_external_service.controller;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.DocumentRequestDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.SowService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sow")
@CrossOrigin(origins = "*")
public class SowController {
    @Autowired
    private SowService sowService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateDocument(@RequestBody DocumentRequestDTO request) {
        try {
            String filePath = sowService.generateDocument(request);
            String fileName = Paths.get(filePath).getFileName().toString();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Document generated successfully");
            response.put("file_name", fileName);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to generate document");
            error.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/download/{fileName}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("/app/download").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

