package com.ifmedtech.apps.ifone.ifone_spring_external_service.service.sections;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.service.WordTableBuilder;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;

import static com.ifmedtech.apps.ifone.ifone_spring_external_service.service.WordUtils.*;

public class SetupProjectSection {

    // Method to add the Objectives and Project Background section
    public static void addObjectivesAndProjectBackground(XWPFDocument doc, String projectObjectives, String projectBackground, String existingProductsTable) {

        // Insert page break before starting this section
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true);

        // Add heading for "Objectives and Project Background"
        addHeading(doc, "1. Objectives and Project Background");

        // Add Project Objectives
        addSubHeading(doc, "1. Project Objectives");
        addParagraph(doc, projectObjectives, false);

        // Add Project Background
        addSubHeading(doc, "2. Project Background");
        formatProjectBackground(doc, projectBackground);

        doc.createParagraph();

        // Add Document Objective
        addSubHeading(doc, "3. Document Objective");
        addParagraph(doc, "To define the specific tasks, deliverables, timelines, and responsibilities for a project or service engagement.", false);

        //Existing Products table
        addExistingProductsTable(doc, existingProductsTable);
    }

    public static void formatProjectBackground(XWPFDocument doc, String background) {
        String[] lines = background.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^2\\.\\d .*")) {
                // Subsection heading like 2.1 Key Mechanisms
                doc.createParagraph();
                addBulletPointHeading(doc, line, true);
            } else if (line.startsWith("-")) {
                // Bullet point
                addListBulletPoint(doc, line.substring(1).trim());
            } else if (!line.isEmpty()) {
                // Regular paragraph
                doc.createParagraph();
                addBulletPointHeading(doc, line ,false);
            }
        }
    }

    // Method to add the Existing Products table
    private static void addExistingProductsTable(XWPFDocument doc, String existingProductsTable) {
        List<List<String>> existingProductsData = getSingleListFromText(existingProductsTable);
        if (!existingProductsData.isEmpty()) {
            addSubHeading(doc, "4. Existing Products");
            doc.createParagraph();
            WordTableBuilder.createGenericTable(doc, existingProductsData);
        } else {
            System.out.println("No existing products data found.");
        }
    }
}
