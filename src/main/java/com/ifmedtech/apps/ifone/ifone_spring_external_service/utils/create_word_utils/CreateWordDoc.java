package com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.sections.*;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateWordDoc {

    public static String createDocument(String fileName, String title, String parties, String projectObjectives, String projectBackground, String existingProductsTable, String requirements, String keyFeatures, String proposedValue, String exclusions, String assumptions, String timelineTable, String iso) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        // Setup Document
        SetupWordDoc.setupDocument(doc, title, parties);

        // Project Section
        SetupProjectSection.addObjectivesAndProjectBackground(doc, projectObjectives, projectBackground, existingProductsTable);

        // Brief Requirements Section
        SetupRequirementsSection.addBriefRequirementsSection(doc, requirements, keyFeatures, proposedValue, exclusions, assumptions, iso);

        // Milestone timeline section
        SetupTimelineSection.addTimelineSection(doc, timelineTable, title);

        // Outcome/Deliverables table
        SetupOutcomeSection.addOutcomeSection(doc);

        // Budget and Path-Forward Section
        SetupBudgetAndPathForwardSection.addBudgetAndPathForwardSection(doc, parties);

        // Save the document
        String filePath = "documents/" + fileName;
        Files.createDirectories(Paths.get("documents"));
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        }
        return filePath;
    }
}
