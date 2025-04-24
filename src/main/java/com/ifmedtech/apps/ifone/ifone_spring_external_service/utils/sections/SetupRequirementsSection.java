package com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.sections;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordTableBuilder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordUtils.*;
import static com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordUtils.addSubHeading;

public class SetupRequirementsSection {

    // Method to add the Objectives and Project Background section
    public static void addBriefRequirementsSection(XWPFDocument doc, String requirements, String keyFeaturesTableData, String proposedValue, String exclusions, String assumptions, String iso) {

        // Insert page break before starting this section
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true);

        // Section Heading
        addHeading(doc, "2. Brief Requirements");

        // Section Introduction
        addParagraph(doc, requirements, false);

        // 1. Key Features
        addKeyFeaturesTable(doc, keyFeaturesTableData);
        doc.createParagraph();

        // 2. Proposed Value Additions
        addSubHeading(doc, "2. Proposed Value Additions for Project");
        for (String point : cleanBulletCode(proposedValue)) {
            addPoints(doc, point);
        }

        // 3. Exclusions
        addSubHeading(doc, "3. Exclusions");
        for (String point : cleanBulletCode(exclusions)) {
            addPoints(doc, point);
        }

        // 4. Assumptions
        addSubHeading(doc, "4. Assumptions");
        for (String point : cleanBulletCode(assumptions)) {
            addPoints(doc, point);
        }

        // 5. Dependencies (Static)
        addSubHeading(doc, "5. Dependencies");
        addPoints(doc, "Dependencies from foreign suppliers (if any).");
        addPoints(doc, "Timelines proposed by testing labs during the verification stage.");

        // 6. Regulatory Standards and Project Risk Assessment & Strategy
        addSubHeading(doc, "6. Regulatory Standards and Project Risk Assessment & Strategy");
        addParagraph(doc, "Applicable Standards to the product (for reference purpose only):", true);
        addParagraph(doc, "International Standards", true);

        for (String point : cleanIsoBulletPoints(iso)) {
            addPoints(doc, point);
        }

        // Regulatory Pathway Steps
        addSubHeading(doc, "Regulatory Pathway as per IMDR, 2017 (India)");
        addParagraph(doc, "Following pathway can be followed for receiving manufacturing license", true);

        addParagraph(doc, "Step 1: Create an account in CDSCO with below information", true);
        List<String> step1Points = Arrays.asList(
                "Id proof details",
                "Undertaking (available in CDSCO website)",
                "Corporate Address Proof Details (Certificate of Incorporation)",
                "DSC",
                "Copy of Manufacturing License and the Wholesale Licenses (If not then upload the justification for the same)"
        );
        step1Points.forEach(point -> addPoints(doc, point));

        addParagraph(doc, "Step 2: Apply for MD-12 form to get license to manufacture medical device to perform evaluation\nFrom: Company\nTo: CDSCO Expected duration of reply: 30 - 45 days", false);

        addParagraph(doc, "Step 3: Complete verification testing", false);

        addParagraph(doc, "Step 4: Apply MD-22 for clinical investigation (Not in current scope)\nFrom: Company\nTo: CDSCO\nExpected duration of reply: 90 days (excluding approval from ethics committee)",false);

        addParagraph(doc, "Step 5: Implement a quality management system. Complete Plant Master File\nNote: For completing the implementation of the quality management system, Company must have", false);
        List<String> step5Points = Arrays.asList(
                "Various departments (e.g.: purchase, D&D, HR etc) and corresponding manpower",
                "A Management Representative that will coordinate on behalf of your organisation during the external audit"
        );
        step5Points.forEach(point -> addPoints(doc, point));

        addParagraph(doc, "Step 6: Apply for manufacturing license MD-3\nFrom: Company\nTo: CDSCO\nExpected duration of reply: Document scrutiny: 45 days + audit in 90 days + audit report in 30 days + license in 20 days", false);
    }

    // Method to add the Key Features table
    private static void addKeyFeaturesTable(XWPFDocument doc, String keyFeaturesTableData) {
        List<List<String>> keyFeaturesTable = getSingleListFromText(keyFeaturesTableData);
        if (!keyFeaturesTableData.isEmpty()) {
            addSubHeading(doc, "1. Key Features");
            doc.createParagraph();
            WordTableBuilder.createGenericTable(doc, keyFeaturesTable);
        } else {
            System.out.println("No existing products data found.");
        }
    }

    private static List<String> cleanBulletCode(String text) {
        return Arrays.stream(text.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> {
                    // Remove any leading bullet/dash characters
                    return line.replaceFirst("^[•●\\-–]+\\s*", "").trim();
                })
                .collect(Collectors.toList());
    }

    private static List<String> cleanIsoBulletPoints(String text) {
        return Arrays.stream(text.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.replaceFirst("^[•●\\-–]+\\s*", "")) // remove leading bullet
                .map(line -> {
                    // Split at first dash with spacing
                    String[] parts = line.split(" - ", 2);
                    if (parts.length > 0) {
                        String isoCode = parts[0].replaceAll("^\\*\\*|\\*\\*$", "").trim();
                        String description = parts.length > 1 ? parts[1].replaceAll("-+$", "").trim() : "";
                        return isoCode + " – " + description;
                    }
                    return line;
                })
                .collect(Collectors.toList());
    }

}
