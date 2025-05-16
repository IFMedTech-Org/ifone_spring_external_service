package com.ifmedtech.apps.ifone.ifone_spring_external_service.repository;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.entity.PrescriptionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrescriptionRecordRepository extends JpaRepository<PrescriptionRecordEntity, UUID> {
}
