package com.ifmedtech.apps.ifone.ifone_spring_external_service.dto;

import lombok.Data;

@Data
public class PrescriptionGeminiInputDTO {

    private String base64Image;
    private String doctorName;
    private String deviceId;
}
