package com.ifmedtech.apps.ifone.ifone_spring_external_service.model;

public class SowWordDocumentData {
    public String fileName;
    public String title;
    public String parties;
    public String projectObjectives;
    public String projectBackground;
    public String existingProductsTable;
    public String requirements;
    public String keyFeatures;
    public String proposedValue;
    public String exclusions;
    public String assumptions;
    public String timelineTable;
    public String iso;

    public SowWordDocumentData(String fileName, String title, String parties,
                               String projectObjectives, String projectBackground, String existingProductsTable,
                               String requirements, String keyFeatures, String proposedValue,
                               String exclusions, String assumptions, String timelineTable, String iso) {
        this.fileName = fileName;
        this.title = title;
        this.parties = parties;
        this.projectObjectives = projectObjectives;
        this.projectBackground = projectBackground;
        this.existingProductsTable = existingProductsTable;
        this.requirements = requirements;
        this.keyFeatures = keyFeatures;
        this.proposedValue = proposedValue;
        this.exclusions = exclusions;
        this.assumptions = assumptions;
        this.timelineTable = timelineTable;
        this.iso = iso;
    }
}

