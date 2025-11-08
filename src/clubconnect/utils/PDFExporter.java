package clubconnect.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for exporting data and pie charts to elegant PDF reports.
 * Requires iText JAR in your classpath.
 */
public class PDFExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(33, 150, 243));
    private static final Font SECTION_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(55, 71, 79));
    private static final Font TEXT_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

    // Path to your favorite logo
    private static final String LOGO_PATH = "clubconnect_logo.png";

    // ------------------------------------
    // Generic Table Generator
    // ------------------------------------
    private static void addTable(Document doc, String title, List<Map<String, Object>> data) throws DocumentException {
        if (data == null || data.isEmpty()) {
            Paragraph noData = new Paragraph("No data available.\n\n", TEXT_FONT);
            noData.setAlignment(Element.ALIGN_CENTER);
            doc.add(noData);
            return;
        }

        Map<String, Object> firstRow = data.get(0);
        PdfPTable table = new PdfPTable(firstRow.size());
        table.setWidthPercentage(100);

        // Header row
        for (String key : firstRow.keySet()) {
            PdfPCell headerCell = new PdfPCell(new Phrase(key.toUpperCase(), HEADER_FONT));
            headerCell.setBackgroundColor(new BaseColor(33, 33, 33));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);
        }

        // Data rows (alternating colors)
        boolean alternate = false;
        for (Map<String, Object> row : data) {
            for (Object val : row.values()) {
                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(val), CELL_FONT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                if (alternate) cell.setBackgroundColor(new BaseColor(245, 245, 245));
                table.addCell(cell);
            }
            alternate = !alternate;
        }

        doc.add(new Paragraph(title, SECTION_HEADER_FONT));
        doc.add(new Paragraph("Generated on: " + new java.util.Date(), TEXT_FONT));
        doc.add(Chunk.NEWLINE);
        doc.add(table);
        doc.add(Chunk.NEWLINE);
    }

    // ------------------------------------
    // Attendance / Financial Reports
    // ------------------------------------
    public static void exportAttendanceReport(File file, List<Map<String, Object>> attendanceData) {
        export(file, "Attendance Report", attendanceData);
    }

    public static void exportFinancialReport(File file, List<Map<String, Object>> financialData) {
        export(file, "Financial Report", financialData);
    }

    private static void export(File file, String title, List<Map<String, Object>> data) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add logo if available
            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception ex) {
                System.out.println("Logo not found, continuing without it...");
            }

            Paragraph header = new Paragraph("ClubConnect " + title, TITLE_FONT);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            addTable(document, title, data);

            System.out.println(title + " PDF created: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    // ------------------------------------
    // Pie Chart Exporter (Budget / Financial Visualization)
    // ------------------------------------
    public static void exportPieChart(File file, File imageFile, Map<String, Double> chartData, String reportType) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add logo
            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception ex) {
                System.out.println("Logo not found, continuing without it...");
            }

            // Title
            String title = "ClubConnect " + reportType + " Report (Pie Chart)";
            Paragraph header = new Paragraph(title, TITLE_FONT);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("Generated on: " + new java.util.Date(), TEXT_FONT));
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // Chart Image
            Image chartImg = Image.getInstance(imageFile.getAbsolutePath());
            chartImg.scaleToFit(PageSize.A4.getWidth() - 60, 300);
            chartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(chartImg);
            document.add(Chunk.NEWLINE);

            // Breakdown section
            Paragraph sectionTitle = new Paragraph("💰 Data Breakdown", SECTION_HEADER_FONT);
            document.add(sectionTitle);
            document.add(Chunk.NEWLINE);

            // Filter approved entries if key contains "approved"
            Map<String, Double> approvedData = chartData.entrySet().stream()
                    .filter(e -> e.getKey().toLowerCase().contains("approved"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            double total = approvedData.values().stream().mapToDouble(Double::doubleValue).sum();

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(90);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);

            String[] headers = {"Category", "Amount (LSL)", "Percentage"};
            for (String h : headers) {
                PdfPCell hCell = new PdfPCell(new Phrase(h, HEADER_FONT));
                hCell.setBackgroundColor(new BaseColor(55, 71, 79));
                hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                hCell.setPadding(6);
                table.addCell(hCell);
            }

            boolean alt = false;
            for (Map.Entry<String, Double> entry : approvedData.entrySet()) {
                double value = entry.getValue();
                double percent = total > 0 ? (value / total) * 100 : 0;
                BaseColor bg = alt ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
                table.addCell(makeCell(entry.getKey(), TEXT_FONT, bg));
                table.addCell(makeCell(String.format("LSL %.2f", value), TEXT_FONT, bg));
                table.addCell(makeCell(String.format("%.1f%%", percent), TEXT_FONT, bg));
                alt = !alt;
            }

            document.add(table);

            Paragraph footer = new Paragraph("Total (Approved): LSL " + String.format("%.2f", total),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(46, 125, 50)));
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            document.add(Chunk.NEWLINE);

            Paragraph note = new Paragraph("Report generated automatically by ClubConnect.",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);

            System.out.println("Pie Chart PDF created: " + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    private static PdfPCell makeCell(String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }
}
