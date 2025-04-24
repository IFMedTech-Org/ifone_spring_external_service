package com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.sections;

import com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordTableBuilder;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.List;

import static com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordUtils.*;

public class SetupTimelineSection {

    // Method to Set up the Outcome section
    public static void addTimelineSection(XWPFDocument doc, String timelineTableData, String projectName) {

        // Insert page break before starting this section
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true);

        // Add heading for "Timeline and Activities"
        addHeading(doc, "3. Timeline and Activities");

        // Add Overall Timeline
        addSubHeading(doc, "1. Overall Timeline");
        doc.createParagraph();

        //Milestone Timeline table
        addMilestoneTable(doc, timelineTableData, projectName);
    }

    // Method to add the Timeline Milestone table
    private static void addMilestoneTable(XWPFDocument doc, String timelineTable, String title) {
        if (timelineTable != null && !timelineTable.isEmpty()) {
            List<List<String>> parsedTimelineTable = getMultipleListFromText(timelineTable);
            if (parsedTimelineTable.isEmpty()) {
                System.out.println("Parsed timeline table data is empty!");
            } else {
                WordTableBuilder.createDetailedTimelineTable(doc, parsedTimelineTable, title);
            }
        } else {
            System.out.println("timeline table data is empty!");
        }
    }
}
