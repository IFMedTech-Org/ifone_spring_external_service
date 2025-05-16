package com.ifmedtech.apps.ifone.ifone_spring_external_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "prescription_metadata")
public class PrescriptionMetaDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "image_path")
    private String imagePath;
}
