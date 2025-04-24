package com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.sections;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import static com.ifmedtech.apps.ifone.ifone_spring_external_service.utils.create_word_utils.WordUtils.*;

public class SetupBudgetAndPathForwardSection {

    public static void addBudgetAndPathForwardSection(XWPFDocument doc, String collaboratorCompanyName) {

        // Insert page break before starting this section
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true);

        // Section Heading
        addHeading(doc, "5. Budget and Path-forward");
        doc.createParagraph();

        // Budget Paragraph
        XWPFParagraph budgetPara = doc.createParagraph();
        budgetPara.setIndentationLeft(Units.toEMU(0.03));
        XWPFRun run1 = budgetPara.createRun();
        run1.setBold(true);
        run1.setText("Budget: ");
        run1.setFontSize(12);
        run1.setFontFamily("Calibri");

        XWPFRun run2 = budgetPara.createRun();
        run2.setText("Project cost = Rs. ______ Lakhs (plus taxes extra). Discounted (25%) project cost = Rs. ______ Lakhs (plus taxes extra).");
        run2.setFontSize(10);
        run2.setFontFamily("Calibri");

        doc.createParagraph();

        // Path Forward Paragraph
        XWPFParagraph pathPara = doc.createParagraph();
        pathPara.setIndentationLeft(Units.toEMU(0.03));
        XWPFRun run3 = pathPara.createRun();
        run3.setBold(true);
        run3.setText("Path Forward: ");
        run3.setFontSize(12);
        run3.setFontFamily("Calibri");

        XWPFRun run4 = pathPara.createRun();
        run4.setText("(Please note the above proposal is subject to approval from both parties).");
        run4.setFontSize(10);
        run4.setFontFamily("Calibri");

        doc.createParagraph();

        // Bullets for path-forward steps
        addPoints(doc, "Review of statement of work, corrections if required from " + collaboratorCompanyName + ".");
        addPoints(doc, "Letter of interest from " + collaboratorCompanyName + " for further pursuing the proposal.");
        addPoints(doc, "Project agreement between " + collaboratorCompanyName + " and IF Medtech Pvt Ltd.");

        doc.createParagraph();

        // Note Section
        XWPFParagraph notePara = doc.createParagraph();
        XWPFRun runNote = notePara.createRun();
        runNote.setBold(true);
        runNote.setText("Note : ");
        runNote.setFontSize(10);
        runNote.setFontFamily("Calibri");

        XWPFRun runNoteText = notePara.createRun();
        runNoteText.setText("To sell the product in India, a manufacturing license may be required to be obtained from CDSCO which involves submission of Device Master File and Plant Master file. Current scope of works covers creation of Device Master File and Plant Master file. In case IF Medtech Pvt Ltd is undertaking manufacturing it will have to take activities related to the Plant master file such as implementation of the quality management system which can be initiated after completion of prototype stage (TRL 4).");
        runNoteText.setFontSize(10);
        runNoteText.setFontFamily("Calibri");
        notePara.setAlignment(ParagraphAlignment.BOTH);
    }

}
