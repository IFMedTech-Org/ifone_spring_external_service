package com.ifmedtech.apps.ifone.ifone_spring_external_service.repository;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.entity.PrescriptionMetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrescriptionMetaDataRepository extends JpaRepository<PrescriptionMetaDataEntity, UUID> {
}
