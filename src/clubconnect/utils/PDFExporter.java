/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Utility class for exporting data to PDF reports.
 * Requires iText JAR in your classpath.
 */
public class PDFExporter {

    // -------------------------------
    // Generic Table Generator
    // -------------------------------
    private static void addTable(Document doc, String title, List<Map<String, Object>> data) throws DocumentException {
        if (data == null || data.isEmpty()) {
            Paragraph noData = new Paragraph("No data available.\n\n");
            noData.setAlignment(Element.ALIGN_CENTER);
            doc.add(noData);
            return;
        }

        // Table header (keys from the first row)
        Map<String, Object> firstRow = data.get(0);
        PdfPTable table = new PdfPTable(firstRow.size());
        table.setWidthPercentage(100);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

        // Header row
        for (String key : firstRow.keySet()) {
            PdfPCell headerCell = new PdfPCell(new Phrase(key.toUpperCase(), headerFont));
            headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }

        // Data rows
        for (Map<String, Object> row : data) {
            for (Object val : row.values()) {
                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(val), cellFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        doc.add(new Paragraph(title, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
        doc.add(new Paragraph("Generated on: " + new java.util.Date() + "\n\n"));
        doc.add(table);
        doc.add(Chunk.NEWLINE);
    }

    // -------------------------------
    // Attendance Report Exporter
    // -------------------------------
    public static void exportAttendanceReport(File file, List<Map<String, Object>> attendanceData) {
        export(file, "Attendance Report", attendanceData);
    }

    // -------------------------------
    // Financial Report Exporter
    // -------------------------------
    public static void exportFinancialReport(File file, List<Map<String, Object>> financialData) {
        export(file, "Financial Report", financialData);
    }

    // -------------------------------
    // Core Export Logic
    // -------------------------------
    private static void export(File file, String title, List<Map<String, Object>> data) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Title
            Paragraph header = new Paragraph("ClubConnect " + title,
                    new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE));
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            // Add table
            addTable(document, title, data);

            document.close();
            System.out.println(title + " PDF created: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}
