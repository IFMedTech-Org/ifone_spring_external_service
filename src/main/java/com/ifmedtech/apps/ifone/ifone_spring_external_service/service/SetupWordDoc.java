package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFFooter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SetupWordDoc {

    public static void setupDocument(XWPFDocument doc, String productName, String collaboratorCompanyName) {
        // Set margins
        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(1500));
        pageMar.setRight(BigInteger.valueOf(1500));

        // Add title
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = title.createRun();
        run.setText("Statement of Work");
        run.setFontSize(23);
        run.setBold(true);
        run.setFontFamily("Calibri");
        run.setColor("7F7F7F"); // Gray
        run.addBreak();

        // Version and Date
        XWPFParagraph version = doc.createParagraph();
        version.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun versionRun = version.createRun();
        versionRun.setText("Version 1");
        versionRun.setFontSize(12);
        versionRun.setColor("0079BF"); // Blue
        versionRun.setFontFamily("Calibri");

        XWPFParagraph date = doc.createParagraph();
        date.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun dateRun = date.createRun();
        dateRun.setText("(" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ")");
        dateRun.setFontSize(12);
        dateRun.setColor("0079BF");
        dateRun.setFontFamily("Calibri");

        for (int i = 0; i < 3; i++) {
            doc.createParagraph().createRun().addBreak(); // Spacing
        }

        setupDocumentHeader(doc, productName);

        // --- Add vertical spacing to push next section to bottom of page ---
        for (int i = 0; i < 10; i++) { // You may need to fine-tune this based on font sizes
            doc.createParagraph().createRun().addBreak();
        }

        // --- Submitted to/by section (visually near bottom of page 1, not in footer) ---
        addSubmissionSection(doc, "Submitted to", collaboratorCompanyName);
        addSubmissionSection(doc, "Submitted by", "IF MEDTECH Pvt Ltd");

        // --- Insert page break so contents start on next page ---
        XWPFParagraph pageBreakPara = doc.createParagraph();
        pageBreakPara.setPageBreak(true); // Page break here

        // Contents header
        XWPFParagraph contentsHeader = doc.createParagraph();
        contentsHeader.setStyle("Heading1");
        XWPFRun contentsRun = contentsHeader.createRun();
        contentsRun.setText("Contents");
        contentsRun.setFontSize(16);
        contentsRun.setBold(true);

        doc.createParagraph();

        // Table of contents (manual entries)
        String[] contents = {
                "1. Objectives and Project Background",
                "2. Brief Requirements",
                "3. Timeline and Activities",
                "4. Outcome/Deliverables",
                "5. Budget & Path Forward"
        };

        for (String item : contents) {
            XWPFParagraph para = doc.createParagraph();
            para.setIndentationLeft(400);
            XWPFRun r = para.createRun();
            r.setText(item);
            r.setFontSize(11);
            r.setBold(true);
            r.setFontFamily("Calibri");
            // Ensure there's a line break between the items if needed
            para.createRun().addBreak();  // Add a line break after each content point (optional)
        }

        // Footer (manual — POI doesn't support footers like python-docx)
        addFooter(doc);
    }

    private static void setupDocumentHeader(XWPFDocument doc, String productName) {
        // Create the table (single-cell, one row)
        XWPFTable table = doc.createTable(1, 1);
        XWPFTableRow row = table.getRow(0);
        XWPFTableCell cell = row.getCell(0);

        // Set the table width to 100% so it spans the entire width of the page
        table.setWidth("100%");

        // Remove table borders to make it look like a simple box
        table.getCTTbl().getTblPr().unsetTblBorders();

        // Remove any padding inside the cell to make it stretch fully
        CTTc ctTc = cell.getCTTc();
        CTTcPr tcPr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();

        // Set the background color of the cell (light blue)
        CTShd shd = tcPr.addNewShd();
        shd.setFill("CCECFF");  // Light blue color

        // Add the product name inside the cell
        CTTcMar cellMar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();
        WordTableBuilder.setPaddingForSide(cellMar, "Top", 500);
        WordTableBuilder.setPaddingForSide(cellMar, "Bottom", 500);
        WordTableBuilder.setPaddingForSide(cellMar, "Left", 200);
        XWPFParagraph cellParagraph = cell.getParagraphs().getFirst();
        cellParagraph.setAlignment(ParagraphAlignment.LEFT);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFRun cellRun = cellParagraph.createRun();
        cellRun.setText(productName);
        cellRun.setBold(true);
        cellRun.setFontSize(28);
        cellRun.setFontFamily("Calibri");
        cellRun.setColor("595959");

    }

    private static void addSubmissionSection(XWPFDocument doc, String label, String name) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r1 = p.createRun();
        r1.setText(label);
        r1.setBold(true);
        r1.setFontSize(12);
        r1.setColor("7F7F7F");
        r1.setFontFamily("Calibri");

        XWPFRun r2 = p.createRun();
        r2.addBreak();
        r2.setText(name);
        r2.setBold(true);
        r2.setFontSize(18);
        r2.setColor("0079BF");
        r2.setFontFamily("Calibri");

        doc.createParagraph();
    }

    private static void addFooter(XWPFDocument doc) {
        // Ensure section properties exist
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        XWPFHeaderFooterPolicy footerPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
        XWPFFooter footer = footerPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

        // Create a table with 2 rows, 1 column
        XWPFTable table = footer.createTable(2, 1);
        table.setWidth("100%");

        // Hide borders
        CTTblBorders borders = table.getCTTbl().getTblPr().addNewTblBorders();
        setBorderInvisible(borders.addNewTop());
        setBorderInvisible(borders.addNewLeft());
        setBorderInvisible(borders.addNewRight());
        setBorderInvisible(borders.addNewBottom());
        setBorderInvisible(borders.addNewInsideH());
        setBorderInvisible(borders.addNewInsideV());

        // === Row 1: Page Number ===
        XWPFTableCell row1Cell = table.getRow(0).getCell(0);
        XWPFParagraph p1 = row1Cell.getParagraphs().getFirst();
        p1.setAlignment(ParagraphAlignment.RIGHT);
        row1Cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFRun rPage = p1.createRun();
        rPage.setText("Page ");
        rPage.setFontSize(9);
        rPage.setFontFamily("Calibri");
        p1.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");

        // === Row 2: Confidential Text ===
        XWPFTableCell row2Cell = table.getRow(1).getCell(0);
        XWPFParagraph p2 = row2Cell.getParagraphs().getFirst();
        p2.setAlignment(ParagraphAlignment.CENTER);
        row2Cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

        XWPFRun r1 = p2.createRun();
        r1.setText("CONFIDENTIAL");
        r1.setFontSize(10);
        r1.setColor("C00000");
        r1.setBold(true);
        r1.setFontFamily("Calibri");
        r1.addBreak();

        XWPFRun r2 = p2.createRun();
        r2.setText("Further distribution prohibited without prior written consent");
        r2.setFontSize(9);
        r2.setColor("919191");
        r2.setFontFamily("Calibri");
    }

    private static void setBorderInvisible(CTBorder border) {
        border.setVal(STBorder.SINGLE);
        border.setColor("FFFFFF");
        border.setSz(BigInteger.ZERO);
    }
}
