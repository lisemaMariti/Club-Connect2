/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.utils;

import org.apache.commons.csv.*;
import java.io.*;
import java.util.List;


public class CSVExporter {

    public static void export(String filePath, List<String[]> data, String[] headers) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers))) {
            for (String[] row : data) csvPrinter.printRecord((Object[]) row);
        }
    }
}
