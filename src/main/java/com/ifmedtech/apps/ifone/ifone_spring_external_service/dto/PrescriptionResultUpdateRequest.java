package com.ifmedtech.apps.ifone.ifone_spring_external_service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PrescriptionResultUpdateRequest {

    private List<UpdateItem> updates;

    @Data
    public static class UpdateItem {
        private UUID id;
        private ResultStatus result;

        public enum ResultStatus {
            ACCEPTED,
            REJECTED
        }
    }
}
