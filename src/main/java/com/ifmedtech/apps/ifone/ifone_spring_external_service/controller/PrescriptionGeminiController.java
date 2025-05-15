package com.ifmedtech.apps.ifone.ifone_spring_external_service.controller;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionGeminiInputDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionMedicineResponseDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionResultUpdateRequest;
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
            @RequestBody PrescriptionGeminiInputDTO prescriptionGeminiInputDTO,
            @RequestParam(value = "prompt", defaultValue = "Extract the medications and their dosage with frequency in JSON format") String prompt) {

        try {
            if (prescriptionGeminiInputDTO.getBase64Image() == null || prescriptionGeminiInputDTO.getBase64Image().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid input",
                        "message", "Image is missing or empty"
                ));
            }

            String jsonPayload = fileConverter.convertToGeminiJson(prescriptionGeminiInputDTO.getBase64Image(), prompt);
            List<Map<String, Object>> geminiResponse = geminiService.sendToGemini(jsonPayload);

            List<PrescriptionMedicineResponseDTO> savedItems = geminiService.savePrescriptionToDatabase(prescriptionGeminiInputDTO, geminiResponse);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(savedItems);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Conversion failed",
                            "message", Optional.ofNullable(e.getMessage()).orElse("Unexpected error")
                    ));
        }
    }

    @PostMapping("/update-results")
    public ResponseEntity<?> updateResults(@RequestBody PrescriptionResultUpdateRequest request) {
        try {
            geminiService.updateResults(request);
            return ResponseEntity.ok(Map.of("message", "Results updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Update failed",
                    "message", e.getMessage()
            ));
        }
    }
}
