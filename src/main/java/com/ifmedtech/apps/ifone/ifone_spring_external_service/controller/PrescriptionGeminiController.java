package com.ifmedtech.apps.ifone.ifone_spring_external_service.controller;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionGeminiInputDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.GenerateGeminiPayload;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.PrescriptionGeminiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "*")
public class PrescriptionGeminiController {

    private final PrescriptionGeminiService geminiService;
    private final GenerateGeminiPayload fileConverter;

    public PrescriptionGeminiController(PrescriptionGeminiService geminiService, GenerateGeminiPayload fileConverter) {
        this.geminiService = geminiService;
        this.fileConverter = fileConverter;
    }

    @PostMapping(value = "/process-file", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> convertTextToJson(
            @RequestBody PrescriptionGeminiInputDTO base64ImageInput,  // Accepting the input text directly as a String
            @RequestParam(value = "prompt", defaultValue = "Extract the medications and their dosage with frequency in JSON format") String prompt) {

        try {
            if (base64ImageInput.getInput() == null || base64ImageInput.getInput().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid input",
                        "message", "Image is missing or empty"
                ));
            }

            String jsonPayload = fileConverter.convertToGeminiJson(base64ImageInput.getInput(), prompt);

            List<Map<String, Object>> geminiResponse = geminiService.sendToGemini(jsonPayload);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(geminiResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Conversion failed",
                            "message", Optional.ofNullable(e.getMessage()).orElse("Unexpected error")
                    ));
        }

    }

}
