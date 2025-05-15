package com.ifmedtech.apps.ifone.ifone_spring_external_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PrescriptionMedicineResponseDTO {
    private UUID id;
    private String medication;
    private String dosage;
    private String frequency;
    private String result; // NONE initially
}
