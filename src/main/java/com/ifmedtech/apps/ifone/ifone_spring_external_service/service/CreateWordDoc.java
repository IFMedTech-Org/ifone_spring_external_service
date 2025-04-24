package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.model.SowWordDocumentData;
import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.sections.*;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateWordDoc {

    public static String createDocument(SowWordDocumentData data) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        // Setup Document
        SetupWordDoc.setupDocument(doc, data.title, data.parties);

        // Project Section
        SetupProjectSection.addObjectivesAndProjectBackground(doc, data.projectObjectives, data.projectBackground, data.existingProductsTable);

        // Brief Requirements Section
        SetupRequirementsSection.addBriefRequirementsSection(doc, data.requirements, data.keyFeatures, data.proposedValue, data.exclusions, data.assumptions, data.iso);

        // Milestone timeline section
        SetupTimelineSection.addTimelineSection(doc, data.timelineTable, data.title);

        // Outcome/Deliverables table
        SetupOutcomeSection.addOutcomeSection(doc);

        // Budget and Path-Forward Section
        SetupBudgetAndPathForwardSection.addBudgetAndPathForwardSection(doc, data.parties);

        // Save the document
        String filePath = "documents/" + data.fileName;
        Files.createDirectories(Paths.get("documents"));
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        }
        return filePath;
    }
}
