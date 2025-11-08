/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Map;

public class AttendanceChartPanel extends JPanel {

    public AttendanceChartPanel(Map<String, Integer> attendanceData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : attendanceData.entrySet()) {
            dataset.addValue(entry.getValue(), "Attendance", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Event Attendance",
                "Event",
                "Number of Attendees",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.setLayout(new java.awt.BorderLayout());
        this.add(chartPanel, java.awt.BorderLayout.CENTER);
    }
}
