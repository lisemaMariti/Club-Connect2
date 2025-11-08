/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.ui;

import clubconnect.dao.AttendanceDAO;
import javax.swing.*;
import java.util.Map;

public class AttendanceReportApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, Integer> data = AttendanceDAO.getAttendanceCounts();

            JFrame frame = new JFrame("Attendance Report");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new AttendanceChartPanel(data));
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

