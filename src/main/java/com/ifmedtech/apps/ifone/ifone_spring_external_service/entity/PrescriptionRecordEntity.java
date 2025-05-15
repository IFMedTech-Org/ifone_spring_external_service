package com.ifmedtech.apps.ifone.ifone_spring_external_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "prescription_record")
public class PrescriptionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "medication")
    private String medication;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "frequency")
    private String frequency;

    @Enumerated(EnumType.STRING)
    private ResultStatus result = ResultStatus.NONE;

    private LocalDateTime createdAt;

    public enum ResultStatus {
        ACCEPTED, REJECTED, NONE
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
