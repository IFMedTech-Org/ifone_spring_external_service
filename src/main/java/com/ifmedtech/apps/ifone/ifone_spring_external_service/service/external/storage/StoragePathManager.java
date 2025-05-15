package com.ifmedtech.apps.ifone.ifone_spring_external_service.service.external.storage;

import org.springframework.stereotype.Component;

@Component
public class StoragePathManager {

    public String generatePrescriptionRecordFilePath() {
        return "record/image/";
    }

}
