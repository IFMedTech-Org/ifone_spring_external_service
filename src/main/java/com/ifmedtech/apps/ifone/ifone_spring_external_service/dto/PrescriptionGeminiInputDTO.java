package com.ifmedtech.apps.ifone.ifone_spring_external_service.dto;

public class PrescriptionGeminiInputDTO {

    private String base64Image;

    public String getInput() {
        return base64Image;
    }

    public void setInput(String base64Image) {
        this.base64Image = base64Image;
    }
}
