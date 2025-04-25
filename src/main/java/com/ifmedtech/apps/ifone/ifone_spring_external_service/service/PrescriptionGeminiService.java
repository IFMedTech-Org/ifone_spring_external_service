package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionGeminiService {

    @Value("${gemini.api_key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String modelName;

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

        // Extract JSON text response from AI response
        String jsonText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        // Convert extracted JSON string into a List<Map<String, Object>> since response is an array
        return objectMapper.readValue(jsonText.replaceAll("```json|```", "").trim(), new TypeReference<>() {
        });
    }

}
