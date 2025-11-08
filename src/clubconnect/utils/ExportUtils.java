package clubconnect.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportUtils {

    // CSV Export
    public static void exportCSV(File file, List<Map<String, Object>> data) throws IOException {
        try (PrintWriter pw = new PrintWriter(file)) {
            if (data.isEmpty()) return;
            pw.println(String.join(",", data.get(0).keySet()));
            for (Map<String, Object> row : data) {
                pw.println(row.values().stream().map(String::valueOf).collect(Collectors.joining(",")));
            }
        }
    }

    // PDF Export (simple table)
    public static void exportPDF(File file, String title, List<Map<String, Object>> data) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Generated on: " + new java.util.Date()));
            document.add(Chunk.NEWLINE);

            if (data.isEmpty()) {
                document.add(new Paragraph("No data available."));
            } else {
                PdfPTable table = new PdfPTable(data.get(0).size());
                table.setWidthPercentage(100);

                // Header
                for (String key : data.get(0).keySet()) {
                    PdfPCell cell = new PdfPCell(new Phrase(key));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                // Data rows
                for (Map<String, Object> row : data) {
                    for (Object val : row.values()) {
                        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(val)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                }

                document.add(table);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen()) document.close();
        }
    }
}
