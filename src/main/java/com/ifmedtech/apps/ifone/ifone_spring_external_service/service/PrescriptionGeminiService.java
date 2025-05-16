package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionGeminiInputDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionMedicineResponseDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.PrescriptionResultUpdateRequest;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.entity.PrescriptionMetaDataEntity;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.entity.PrescriptionRecordEntity;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.repository.PrescriptionMetaDataRepository;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.repository.PrescriptionRecordRepository;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.external.storage.ImageStorageService;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.external.storage.StoragePathManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionGeminiService {

    @Value("${spring.ai.gemini.apiKey}")
    private String apiKey;

    @Value("${gemini.model}")
    private String modelName;

    private final PrescriptionRecordRepository prescriptionRecordRepository;
    private final PrescriptionMetaDataRepository prescriptionMetaDataRepository;
    private final ImageStorageService imageStorageService;
    private final GenerateGeminiPayload fileConverter;
    private final StoragePathManager storagePathManager;


    public List<Map<String, Object>> sendToGemini(String jsonPayload) throws Exception {
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                modelName,
                apiKey
        );

        HttpClient client = HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to call Gemini API: " + response.statusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());

        String jsonText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        String cleanedJson = jsonText.replaceAll("```json|```", "").trim();

        JsonNode parsed = objectMapper.readTree(cleanedJson);

        if (parsed.isArray()) {
            return objectMapper.convertValue(parsed, new TypeReference<>() {});
        } else if (parsed.isObject()) {
            return List.of(objectMapper.convertValue(parsed, new TypeReference<>() {}));
        } else {
            throw new RuntimeException("Invalid response format from Gemini");
        }
    }

    @Transactional
    public List<PrescriptionMedicineResponseDTO> savePrescriptionToDatabase(PrescriptionGeminiInputDTO inputDTO, List<Map<String, Object>> geminiResponse) {
        List<PrescriptionRecordEntity> savedRecords = new ArrayList<>();

        String mimeType = fileConverter.detectMimeType(inputDTO.getBase64Image());
        String extension = imageStorageService.getExtensionFromMime(mimeType);
        String imagePath = imageStorageService.uploadFile(inputDTO.getBase64Image(), storagePathManager.generatePrescriptionRecordFilePath(), extension);

        // Create and save metadata entity
        PrescriptionMetaDataEntity metadata = new PrescriptionMetaDataEntity();
        metadata.setDoctorName(inputDTO.getDoctorName());
        metadata.setDeviceId(inputDTO.getDeviceId());
        metadata.setImagePath(imagePath);
        prescriptionMetaDataRepository.save(metadata); // Make sure this repository is injected

        // Save prescription records with reference to metadata
        for (Map<String, Object> item : geminiResponse) {
            PrescriptionRecordEntity entity = new PrescriptionRecordEntity();
            entity.setMetadata(metadata);
            entity.setResult(PrescriptionRecordEntity.ResultStatus.NONE);
            entity.setMedication((String) item.get("medication"));
            entity.setDosage((String) item.get("dosage"));
            entity.setFrequency((String) item.get("frequency"));
            prescriptionRecordRepository.save(entity);
            savedRecords.add(entity);
        }

        return savedRecords.stream().map(this::toDto).collect(Collectors.toList());
    }

    private PrescriptionMedicineResponseDTO toDto(PrescriptionRecordEntity entity) {
        PrescriptionMedicineResponseDTO dto = new PrescriptionMedicineResponseDTO();
        dto.setId(entity.getId());
        dto.setMedication(entity.getMedication());
        dto.setDosage(entity.getDosage());
        dto.setFrequency(entity.getFrequency());
        dto.setResult(entity.getResult().name());
        return dto;
    }

    @Transactional
    public void updateResults(PrescriptionResultUpdateRequest request) {
        for (PrescriptionResultUpdateRequest.UpdateItem item : request.getUpdates()) {
            PrescriptionRecordEntity record = prescriptionRecordRepository.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Prescription ID not found: " + item.getId()));

            record.setResult(PrescriptionRecordEntity.ResultStatus.valueOf(item.getResult().name()));
        }
    }
}
