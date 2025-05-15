package com.ifmedtech.apps.ifone.ifone_spring_external_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "prescription_record")
public class PrescriptionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctorName;
    private String deviceId;
    private String imagePath;

    @Lob
    private String prescriptionJson;

    @Enumerated(EnumType.STRING)
    private ResultStatus result;

    private LocalDateTime createdAt;

    public enum ResultStatus {
        ACCEPTED, REJECTED, NONE
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
