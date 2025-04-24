package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.dto.DocumentRequestDTO;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.model.SowWordDocumentData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SowService {
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final String PROMPT_FILE = "src/main/resources/sow_prompt.json";
    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> loadPrompts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(PROMPT_FILE), new TypeReference<>() {
        });
    }

    public String loadInputData(DocumentRequestDTO request) {
        return String.format("""
                Project Title: %s
                Submitting and Receiving Parties: %s
                Objective Summary: %s
                Key Features or Requirements: %s
                """, request.getTitle(), request.getParties(), request.getObjectives(), request.getFeatures());
    }

//    Async Api call (all at same time)
    public String generateDocument(DocumentRequestDTO request) throws IOException {
        Map<String, String> prompts = loadPrompts();
        String inputData = loadInputData(request);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String fileName = "SOW_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + "_" + uniqueId + ".docx";

        ExecutorService executor = Executors.newFixedThreadPool(10); // Thread pool

        CompletableFuture<String> projectObjectives = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("project_objectives"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> projectBackground = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("project_background"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> existingProducts = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("existing_products"), inputData, "gpt-4o"), executor);
        CompletableFuture<String> briefReq = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("brief_req"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> replyKey = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("reply_key"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> replyValue = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("reply_value"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> exclusions = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("exclusions"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> assumptions = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("assumptions"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> timeline = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("timeline"), inputData, "gpt-4o-mini"), executor);
        CompletableFuture<String> iso = CompletableFuture.supplyAsync(() -> callOpenAI(prompts.get("iso"), inputData, "gpt-4o-mini"), executor);

        // Wait for all to complete
        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                projectObjectives, projectBackground, existingProducts, briefReq,
                replyKey, replyValue, exclusions, assumptions, timeline, iso
        );

        allDone.join(); // Block until all are done

        executor.shutdown(); // Clean up the thread pool

        SowWordDocumentData documentData = new SowWordDocumentData(
                fileName,
                request.getTitle(),
                request.getParties(),
                projectObjectives.join(),
                projectBackground.join(),
                existingProducts.join(),
                briefReq.join(),
                replyKey.join(),
                replyValue.join(),
                exclusions.join(),
                assumptions.join(),
                timeline.join(),
                iso.join()
        );

        return CreateWordDoc.createDocument(documentData);
    }

    private String callOpenAI(String prompt, String inputData, String model) {

        String apiKey = openaiApiKey;
        if (apiKey == null || apiKey.isEmpty()) {
            return "Missing OpenAI API key";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", inputData)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);
            Map<?, ?> choice = ((List<Map<String, Object>>) Objects.requireNonNull(response.getBody()).get("choices")).getFirst();
            return (String) ((Map<?, ?>) choice.get("message")).get("content");

        } catch (Exception e) {
            return "Error calling OpenAI: " + e.getMessage();
        }
    }
}
