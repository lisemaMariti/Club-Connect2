package clubconnect.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

public class BarChartPanel extends JPanel {

    private Map<String, Integer> data;

    public BarChartPanel(Map<String, Integer> data) {
        this.data = data;
        setPreferredSize(new Dimension(600, 400));
        setLayout(null);
        setOpaque(true);
        setBackground(Color.WHITE);

        // --- Buttons inside the same panel ---
        JButton refreshButton = new JButton("Refresh");
        JButton exportButton = new JButton("Export PDF");

        // Set absolute positions
        refreshButton.setBounds(20, 20, 100, 30);
        exportButton.setBounds(130, 20, 120, 30);

        // Add action listener for export PDF
        exportButton.addActionListener(this::exportToPDF);

        add(refreshButton);
        add(exportButton);
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ✅ ensures buttons stay visible

        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int numBars = data.size();
        int max = data.values().stream().max(Integer::compareTo).orElse(1);
        int barWidth = (width - 2 * padding) / Math.max(numBars, 1);

        int i = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int value = entry.getValue();
            int barHeight = (int) ((double) value / max * (height - 2 * padding));

            int x = padding + i * barWidth;
            int y = height - padding - barHeight;

            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(x, y, barWidth - 5, barHeight);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, barWidth - 5, barHeight);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(entry.getKey(), x + 5, height - padding + 15);
            g2.drawString(String.valueOf(value), x + 5, y - 5);

            i++;
        }

        // Axes
        g2.setColor(Color.BLACK);
        g2.drawLine(padding, height - padding, width - padding, height - padding);
        g2.drawLine(padding, height - padding, padding, padding);

        g2.dispose();
    }

    // --- Action logic for "Export PDF" button ---
    private void exportToPDF(ActionEvent evt) {
        try {
            // Get data from MembershipDAO
            List<Map<String, Object>> attendanceData =clubconnect.dao.MembershipDAO.getAttendanceSummary();
            if (attendanceData == null || attendanceData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No attendance records found.");
                return;
            }

            // Export using PDFExporter
            File outFile = new File("attendance_report.pdf");
            clubconnect.utils.PDFExporter.exportAttendanceReport(outFile, attendanceData);

            JOptionPane.showMessageDialog(this,
                    "Attendance report generated successfully!\n" + outFile.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- Demo main method ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bar Chart with Export PDF Button");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 500);

            Map<String, Integer> sampleData = Map.of(
                    "A", 50,
                    "B", 75,
                    "C", 30,
                    "D", 90,
                    "E", 60
            );

            BarChartPanel chartPanel = new BarChartPanel(sampleData);
            frame.add(chartPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
