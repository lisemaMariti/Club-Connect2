package clubconnect.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVExporter {

    public static void export(String filePath, List<String[]> data, String[] headers) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        System.out.println("🔧 Writing plain CSV to: " + file.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Write header
            writer.write(String.join(",", headers));
            writer.newLine();

            // Write data rows
            int rowCount = 0;
            for (String[] row : data) {
                if (row == null) continue;
                String line = String.join(",", sanitize(row));
                writer.write(line);
                writer.newLine();
                rowCount++;
            }

            writer.flush();
            System.out.println("✅ Plain CSV writing completed. Rows written: " + rowCount);
        }
    }

    private static String[] sanitize(String[] row) {
        String[] safe = new String[row.length];
        for (int i = 0; i < row.length; i++) {
            safe[i] = (row[i] == null ? "" : row[i].replace(",", ";"));
        }
        return safe;
    }
}
