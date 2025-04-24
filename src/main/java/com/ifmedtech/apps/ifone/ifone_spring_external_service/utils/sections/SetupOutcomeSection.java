package com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.sections;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordTableBuilder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.List;

import static com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordUtils.*;

public class SetupOutcomeSection {

    // Method to Set up the Outcome section
    public static void addOutcomeSection(XWPFDocument doc) {

        // Insert page break before starting this section
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true);

        // Add heading for "Objectives and Project Background"
        addHeading(doc, "4. Outcome/Deliverables");

        //Existing Products table
        addOutcomeTable(doc);
    }

    // Method to add the Outcomes table
    private static void addOutcomeTable(XWPFDocument doc) {
        List<List<String>> OutcomeDeliverablesData = List.of(
                List.of("1", "Signing of Agreement & Design Inputs", "Project Agreement, Design Inputs", "0", "30% Advance"),
                List.of("2", "Ideation and Proof of Principle", "Design & Regulatory Requirements; Essential Principles of Safety", "8", "10%"),
                List.of("3", "Proof of Concept Fabricated", "Proof of concept", "13", "20%"),
                List.of("4", "Proof of Concept Established", "Prototype Ready + BOM", "20", "20%"),
                List.of("5", "Bench testing", "Device deployment, External Testing + Medical Device File, ISO Documentation", "23", "20%*"),
                List.of("6", "Early stage, Late stage validation, Pre-commercialisation",
                        "Fully functional clinical grade device ready with regulatory dossier for use + Manufacturing lines established. Design for manufacture (DFM) finalised and devices manufactured with DHF + ISO implementation, Manufacturing license from CDSCO and commercial batch manufacturing initiated",
                        "12 additional", "Target Rs 700 per piece (BOM+Mfg) + taxes. (10K devices per year)"),
                List.of("", "", "", "23 + 12 weeks", "100%")
        );
        WordTableBuilder.createOutcomeDeliverablesTable(doc, OutcomeDeliverablesData);
    }
}
