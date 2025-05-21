package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordUtils {

    private static BigInteger bulletNumID = null;

    // A helper method to format paragraphs for the title and other sections
    public static void addHeading(XWPFDocument doc, String text) {
        var heading = doc.createParagraph();
        var run = heading.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("Calibri");
        doc.createParagraph();
    }

    // Helper method to add a paragraph with a specific indentation level
    public static void addSubHeading(XWPFDocument doc, String text) {
        XWPFParagraph subHeading = doc.createParagraph();
        subHeading.setIndentationLeft(Units.toEMU(0.03));
        XWPFRun run = subHeading.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setBold(true);
        run.setFontFamily("Calibri");
    }

    // Helper method to add sub-paragraph with specific text
    public  static void addParagraph(XWPFDocument doc, String text, boolean bold) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        paragraph.setIndentationLeft(Units.toEMU(0.03));
        run.setText(text);
        run.setFontSize(10);
        run.setFontFamily("Calibri");
        doc.createParagraph();
    }

    public  static void addBulletPointHeading(XWPFDocument doc, String text, boolean bold) {
        XWPFParagraph bulletPointHeader = doc.createParagraph();
        XWPFRun run = bulletPointHeader.createRun();
        bulletPointHeader.setIndentationLeft(Units.toEMU(0.03));
        run.setText(text);
        run.setBold(bold);
        run.setFontSize(10);
        run.setFontFamily("Calibri");
    }

    public static void addListBulletPoint(XWPFDocument doc, String text) {
        // Sanitize input to remove newlines
        text = text.replaceAll("[\\r\\n]+", " ");

        doc.createParagraph();  // spacing line
        XWPFParagraph para = doc.createParagraph();
        para.setIndentationLeft(Units.toEMU(0.030));

        String[] parts = text.split(":", 2);

        if (parts.length == 2) {
            String boldText = cleanBoldMarkers(parts[0]);

            XWPFRun boldRun = para.createRun();
            boldRun.setBold(true);
            boldRun.setText(boldText + ": ");
            boldRun.setFontSize(10);
            boldRun.setFontFamily("Calibri");

            XWPFRun normalRun = para.createRun();
            normalRun.setText(parts[1].trim());
            normalRun.setFontSize(10);
            normalRun.setFontFamily("Calibri");
        } else {
            XWPFRun run = para.createRun();
            run.setText(text);
            run.setFontSize(10);
            run.setFontFamily("Calibri");
        }
    }


    public static void addPoints(XWPFDocument doc, String text) {
        initializeBullets(doc);  // sets up bullet style once

        XWPFParagraph para = doc.createParagraph();
        para.setNumID(bulletNumID);  // assigns bullet format
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontSize(10);
        run.setFontFamily("Calibri");
        para.setIndentationLeft(Units.toEMU(0.07));
    }

    public static List<List<String>> getMultipleListFromText(String gptOutput) {
        // Split rows by new line, removing unnecessary ones (like rows starting with "|--")
        String[] rows = gptOutput.split("\n");
        List<String> cleanedRows = new ArrayList<>();

        for (String row : rows) {
            String trimmedRow = row.trim();
            if (!trimmedRow.startsWith("|--") && !trimmedRow.isEmpty()) {
                cleanedRows.add(trimmedRow);
            }
        }

        // Split columns by pipe "|" and clean up each column's content
        List<List<String>> data = getLists(cleanedRows);

        // Handle the headers (first row)
        List<String> headers = data.get(0);
        data = data.subList(1, data.size()); // Remaining rows are the actual data

        int numColumns = headers.size();

        // Normalize data to ensure all rows have the same number of columns
        List<List<String>> normalizedData = new ArrayList<>();
        for (List<String> row : data) {
            while (row.size() < numColumns) {
                row.add(""); // Fill with empty strings if row is shorter
            }
            row = row.subList(0, numColumns); // Ensure the row has no more than `numColumns`
            normalizedData.add(row);
        }

        return normalizedData;
    }

    public static List<List<String>> getLists(List<String> cleanedRows) {
        List<List<String>> data = new ArrayList<>();

        for (String row : cleanedRows) {
            if (row.startsWith("|")) {
                String[] cols = row.split("\\|", -1);  // keep all including empty
                List<String> cleaned = new ArrayList<>();
                for (String col : cols) {
                    cleaned.add(col.trim());
                }

                // Remove empty elements at the start and end (due to leading/trailing '|')
                if (!cleaned.isEmpty() && cleaned.getFirst().isEmpty()) {
                    cleaned.removeFirst();
                }
                if (!cleaned.isEmpty() && cleaned.getLast().isEmpty()) {
                    cleaned.removeLast();
                }

                // Pad or trim to exactly 4 columns
                while (cleaned.size() < 4) cleaned.add("");
                if (cleaned.size() > 4) cleaned = cleaned.subList(0, 4);

                data.add(cleaned);
            }
        }
        return data;
    }

    public static List<List<String>> getSingleListFromText(String tableText) {
        List<List<String>> data = new ArrayList<>();
        String[] lines = tableText.strip().split("\n");

        for (String line : lines) {
            if (line.contains("|")) {
                String[] cells = line.split("\\|");
                List<String> row = Arrays.stream(cells)
                        .map(String::trim)
                        .filter(cell -> !cell.isEmpty())
                        .collect(Collectors.toList());

                // Skip rows made of only dashes
                boolean allDashes = row.stream().allMatch(cell -> cell.matches("-+"));
                if (!allDashes) {
                    data.add(row);
                }
            }
        }
        return data;
    }

    public static String cleanBoldMarkers(String text) {
        // Removes leading and trailing ** if they exist
        if (text.startsWith("**") && text.endsWith("**") && text.length() >= 4) {
            return text.substring(2, text.length() - 2).trim();
        }
        return text.trim();
    }

    private static void initializeBullets(XWPFDocument doc) {
        if (bulletNumID != null) return;  // already initialized

        XWPFNumbering numbering = doc.createNumbering();
        bulletNumID = numbering.addNum(numbering.addAbstractNum(createBulletAbstractNum()));
    }

    private static XWPFAbstractNum createBulletAbstractNum() {
        CTAbstractNum abstractNum = CTAbstractNum.Factory.newInstance();
        abstractNum.setAbstractNumId(BigInteger.valueOf(0));

        CTLvl level = abstractNum.addNewLvl();
        level.setIlvl(BigInteger.ZERO);
        level.addNewNumFmt().setVal(STNumberFormat.BULLET);
        level.addNewLvlText().setVal("•");
        level.addNewLvlJc().setVal(STJc.LEFT);

        // Set proper indentation: left for the whole paragraph, hanging for bullet
        CTInd ind = level.addNewPPr().addNewInd();
        ind.setLeft(BigInteger.valueOf(500));
        ind.setHanging(BigInteger.valueOf(300));

        return new XWPFAbstractNum(abstractNum);
    }
}
