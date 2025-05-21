package com.ifmedtech.apps.ifone.ifone_spring_external_service.service;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordTableBuilder {

    public static void createDetailedTimelineTable(XWPFDocument doc, List<List<String>> timelineData, String projectName) {
        XWPFTable table = doc.createTable();

        setBlackTableBorders(table);

        // === Set table width to 100% ===
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(5000));  // 100% of page width

        // Fix column count to 4
        while (table.getRow(0).getTableCells().size() < 4) {
            table.getRow(0).addNewTableCell();
        }

        // === Add header row ===
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {
                "Milestone (TRL)",
                "Objective",
                "Product Development of " + projectName + " - Activity Description",
                "Duration"
        };

        for (int i = 0; i < 4; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            XWPFParagraph para = cell.getParagraphs().getFirst();
            XWPFRun run = para.createRun();
            run.setText(headers[i]);
            run.setColor("FFFFFF");
            run.setBold(true);
            run.setFontSize(10);
            run.setFontFamily("Calibri");

            para.setAlignment(ParagraphAlignment.CENTER);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            setCellColor(cell, "666666");
        }

        // === Add data rows ===
        Map<Integer, Integer> mergeStart = new HashMap<>();
        for (int col : new int[]{0, 1, 3}) {
            mergeStart.put(col, -1);
        }
        // Row 0 is header

        for (List<String> rowData : timelineData) {
            XWPFTableRow row = table.createRow();
            boolean isMilestoneStart = !rowData.getFirst().isEmpty();
            int thisRowIndex = table.getNumberOfRows() - 1;

            for (int col = 0; col < 4; col++) {
                XWPFTableCell cell = row.getCell(col);
                String text = col < rowData.size() ? rowData.get(col) : "";

                XWPFParagraph para = cell.getParagraphs().getFirst();
                XWPFRun run = para.createRun();
                run.setText(text);
                run.setFontSize(10);
                run.setFontFamily("Calibri");

                para.setAlignment(switch (col) {
                    case 0, 1, 3 -> ParagraphAlignment.CENTER;
                    default -> ParagraphAlignment.LEFT;
                });
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                // Highlight milestone row in blue
                if (isMilestoneStart) {
                    if (col == 0 || col == 1 || col == 3) {
                        setCellColor(cell, "CCE5FF");
                    } else {
                        setCellColor(cell, "CCE5FF");
                        run.setItalic(true); // Italic for description
                    }
                }
            }

            // Handle merging start index logic
            for (int col : new int[]{0, 1, 3}) {
                if (isMilestoneStart) {
                    int startIdx = mergeStart.get(col);
                    if (startIdx != -1 && startIdx + 1 < thisRowIndex) {
                        mergeCellsVertically(table, col, startIdx + 1, thisRowIndex - 1);
                    }
                    mergeStart.put(col, thisRowIndex);
                }
            }

        }

        // Final vertical merges after loop
        int lastRowIdx = table.getNumberOfRows() - 1;
        for (int col : new int[]{0, 1, 3}) {
            int startIdx = mergeStart.get(col);
            if (startIdx != -1 && startIdx + 1 < lastRowIdx) {
                mergeCellsVertically(table, col, startIdx + 1, lastRowIdx);
            }
        }

        int[] colWidths = {100, 100, 5500, 100};
        for (XWPFTableRow row : table.getRows()) {
            for (int i = 0; i < row.getTableCells().size(); i++) {
                XWPFTableCell cell = row.getCell(i);
                CTTcBorders borders = cell.getCTTc().getTcPr().isSetTcBorders() ?
                        cell.getCTTc().getTcPr().getTcBorders() :
                        cell.getCTTc().getTcPr().addNewTcBorders();

                CTBorder border = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
                border.setVal(STBorder.SINGLE);
                border.setSz(BigInteger.valueOf(4));
                border.setColor("000000");
                setColWidth(cell, colWidths[i]);
                setCellPadding(cell, 80);
            }
        }

        int lastRowIndex = table.getNumberOfRows() - 1;
        XWPFTableRow totalRow = table.getRow(lastRowIndex);

        // Merge Objective (col 1) and Description (col 2)
        mergeCellsHorizontally(totalRow);

        // Optional: Center-align merged cell
        XWPFTableCell mergedCell = totalRow.getCell(1);
        XWPFParagraph para = mergedCell.getParagraphs().getFirst();
        para.setAlignment(ParagraphAlignment.CENTER);

    }

    public static void createGenericTable(XWPFDocument doc, List<List<String>> data) {
        if (data == null || data.isEmpty()) return;

        int numCols = data.getFirst().size();
        XWPFTable table = doc.createTable();

        setBlackTableBorders(table);

        // Ensure first row has correct number of cells
        XWPFTableRow headerRow = table.getRow(0);
        while (headerRow.getTableCells().size() < numCols) {
            headerRow.addNewTableCell();
        }

        // Set table width to 100%
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(5000));  // 5000 = 100%

        // Header styling
        List<String> headers = data.getFirst();
        for (int i = 0; i < numCols; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            XWPFParagraph para = cell.getParagraphs().getFirst();
            XWPFRun run = para.createRun();

            run.setText(WordUtils.cleanBoldMarkers(headers.get(i)));
            run.setBold(true);
            run.setFontSize(10);
            run.setFontFamily("Calibri");

            para.setAlignment(ParagraphAlignment.CENTER);
            setCellColor(cell, "CCCCCC");  // Blue header background
            setCellPadding(cell, 80);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }

        // Data rows
        for (int rowIndex = 1; rowIndex < data.size(); rowIndex++) {
            List<String> rowData = data.get(rowIndex);
            XWPFTableRow row = table.createRow();

            for (int colIndex = 0; colIndex < numCols; colIndex++) {
                XWPFTableCell cell = row.getCell(colIndex);
                String text = colIndex < rowData.size() ? rowData.get(colIndex) : "";

                XWPFParagraph para = cell.getParagraphs().getFirst();
                XWPFRun run = para.createRun();
                run.setText(text);
                run.setFontSize(10);
                run.setFontFamily("Calibri");
                if(colIndex == 0) setCellColor(cell, "F2F2F2");
                if(colIndex == 0) run.setBold(true);
                para.setAlignment(ParagraphAlignment.LEFT);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                setCellPadding(cell, 120);
            }
        }
    }

    public static void createOutcomeDeliverablesTable(XWPFDocument doc, List<List<String>> data) {

        XWPFTable table = doc.createTable();

        setBlackTableBorders(table);

        // === Set table width to 100% ===
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(5000));  // 100%

        // === Title Row (merged across all 5 columns) ===
        XWPFTableRow titleRow = table.getRow(0);
        XWPFTableCell titleCell = titleRow.getCell(0);

        // Set text and styling
        titleCell.setText("Technical Milestones & Outcome:");
        XWPFParagraph para = titleCell.getParagraphs().getFirst();
        XWPFRun run = para.createRun();
        run.setBold(true);               // Set bold first
        run.setFontFamily("Calibri");    // Then font
        run.setFontSize(11);             // Size close to 10.5
        run.setColor("FFFFFF");          // Optional: white text
        para.setAlignment(ParagraphAlignment.LEFT);
        setCellColor(titleCell, "404040");
        setCellPadding(titleCell, 80);

        // Set grid span of 5 for full merge
        titleCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(5));

        // Remove any additional cells beyond index 0 (ensure it's a single-cell row)
        while (titleRow.getTableCells().size() > 1) {
            titleRow.removeCell(1); // Remove cells 1 to 4
        }

        // === Subheader row ===
        XWPFTableRow subHeader = table.createRow();
        String[] headers = {"Sl.", "Milestone Name", "Outcome", "Activity end (cumulative weeks)", "Budget (INR lakhs)"};
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell;
            if (i < subHeader.getTableCells().size()) {
                cell = subHeader.getCell(i);
            } else {
                cell = subHeader.addNewTableCell(); // Add cell if it doesn't exist
            }

            XWPFParagraph p = cell.getParagraphs().getFirst();
            XWPFRun r = p.createRun();
            r.setText(headers[i]);
            r.setBold(true);
            r.setFontSize(10);
            r.setFontFamily("Calibri");
            p.setAlignment(ParagraphAlignment.CENTER);
            setCellPadding(cell, 80);
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            setCellColor(cell, "CCE5FF");
        }

        // === Data Rows ===
        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            List<String> rowData = data.get(rowIndex);
            XWPFTableRow row = table.createRow();

            // Ensure 5 cells per row
            while (row.getTableCells().size() < 5) {
                row.addNewTableCell();
            }

            if (rowIndex == data.size() - 1) {
                // === Special handling for the last row ===

                // Merge first 3 cells into one
                XWPFTableCell mergedCell = row.getCell(0);
                XWPFParagraph mergedPara = mergedCell.getParagraphs().getFirst();
                XWPFRun mergedRun = mergedPara.createRun();
                mergedRun.setFontSize(10);
                mergedRun.setFontFamily("Calibri");
                mergedRun.setText(""); // Optional label or keep empty
                mergedPara.setAlignment(ParagraphAlignment.LEFT);
                mergedCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                mergedCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(3));

                // Remove the next two cells (physically remove them to end with 3 total cells)
                row.removeCell(2); // Remove 3rd cell (index 2)
                row.removeCell(1); // Then remove 2nd cell (index 1) — order matters

                // Now row has 3 cells: [merged(0,1,2)], cell 3, cell 4

                // Set text for the last two cells (index 1 and 2 after removals)
                for (int i = 1; i <= 2; i++) {
                    XWPFTableCell cell = row.getCell(i);
                    XWPFParagraph p = cell.getParagraphs().getFirst();
                    XWPFRun r = p.createRun();
                    r.setText(rowData.get(i + 2)); // index 3 and 4 from rowData
                    r.setFontSize(10);
                    r.setBold(true);
                    r.setFontFamily("Calibri");
                    p.setAlignment(ParagraphAlignment.CENTER);
                    setCellPadding(cell, 40);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
            } else {
                // Regular data row
                for (int i = 0; i < rowData.size(); i++) {
                    XWPFTableCell cell = row.getCell(i);
                    XWPFParagraph p = cell.getParagraphs().getFirst();
                    XWPFRun r = p.createRun();
                    r.setText(rowData.get(i));
                    r.setFontSize(10);
                    r.setFontFamily("Calibri");
                    setCellPadding(cell, 80);
                    p.setAlignment(ParagraphAlignment.CENTER);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
            }
        }
    }

    private static void setCellColor(XWPFTableCell cell, String hexColor) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTShd shd = tcPr.isSetShd() ? tcPr.getShd() : tcPr.addNewShd();
        shd.setFill(hexColor);
    }

    private static void mergeCellsHorizontally(XWPFTableRow row) {
        for (int colIndex = 0; colIndex <= 2; colIndex++) {
            XWPFTableCell cell = row.getCell(colIndex);
            CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
            if (colIndex == 0) {
                tcPr.addNewHMerge().setVal(STMerge.RESTART);
            } else {
                tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
            CTVMerge vMerge = tcPr.isSetVMerge() ? tcPr.getVMerge() : tcPr.addNewVMerge();

            if (rowIndex == fromRow) {
                vMerge.setVal(STMerge.RESTART);
            } else {
                vMerge.setVal(STMerge.CONTINUE);
            }
        }
    }

    public static void setColWidth(XWPFTableCell cell, int width) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTblWidth tblWidth = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        tblWidth.setW(BigInteger.valueOf(width));
        tblWidth.setType(STTblWidth.DXA); // DXA = twips
    }

    public static void setCellPadding(XWPFTableCell cell, Integer padding) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTcMar cellMar = tcPr.isSetTcMar() ? tcPr.getTcMar() : tcPr.addNewTcMar();

        // Set padding for each side
        setPaddingForSide(cellMar, "Top", padding);
        setPaddingForSide(cellMar, "Bottom", padding);
        setPaddingForSide(cellMar, "Left", padding);
        setPaddingForSide(cellMar, "Right", padding);
    }

    public static void setPaddingForSide(CTTcMar cellMar, String side, int padding) {
        CTTblWidth sidePadding = switch (side) {
            case "Top" -> cellMar.isSetTop() ? cellMar.getTop() : cellMar.addNewTop();
            case "Bottom" -> cellMar.isSetBottom() ? cellMar.getBottom() : cellMar.addNewBottom();
            case "Left" -> cellMar.isSetLeft() ? cellMar.getLeft() : cellMar.addNewLeft();
            case "Right" -> cellMar.isSetRight() ? cellMar.getRight() : cellMar.addNewRight();
            default -> null;
        };
        // Set padding width for the side
        if (sidePadding != null) {
            sidePadding.setType(STTblWidth.DXA);
            sidePadding.setW(BigInteger.valueOf(padding));
        }
    }

    private static void setBlackTableBorders(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        BigInteger size = BigInteger.valueOf(4); // Thickness of the border
        String color = "000000"; // Black

        CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        top.setVal(STBorder.SINGLE);
        top.setSz(size);
        top.setColor(color);

        CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        bottom.setVal(STBorder.SINGLE);
        bottom.setSz(size);
        bottom.setColor(color);

        CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        left.setVal(STBorder.SINGLE);
        left.setSz(size);
        left.setColor(color);

        CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        right.setVal(STBorder.SINGLE);
        right.setSz(size);
        right.setColor(color);

        CTBorder insideH = borders.isSetInsideH() ? borders.getInsideH() : borders.addNewInsideH();
        insideH.setVal(STBorder.SINGLE);
        insideH.setSz(size);
        insideH.setColor(color);

        CTBorder insideV = borders.isSetInsideV() ? borders.getInsideV() : borders.addNewInsideV();
        insideV.setVal(STBorder.SINGLE);
        insideV.setSz(size);
        insideV.setColor(color);
    }

}
