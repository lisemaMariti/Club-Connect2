package clubconnect.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import clubconnect.utils.PDFExporter;

/**
 * Displays a pie chart of financial/budget data and allows export to PDF.
 */
public class PieChartPanel extends JPanel {
    private Map<String, Double> data;
    private int hoveredSlice = -1;
    private String reportType = "Financial"; // Could be "Budget" or "Financial"

    public PieChartPanel(Map<String, Double> data) {
        this.data = new LinkedHashMap<>(data);
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        ChartArea chart = new ChartArea();
        add(chart, BorderLayout.CENTER);

        JButton exportBtn = new JButton("📄 Export Pie Chart to PDF");
        exportBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportBtn.addActionListener(e -> exportPieChart(chart));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(getBackground());
        bottomPanel.add(exportBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Exports the pie chart as a PDF using PDFExporter.
     */
    private void exportPieChart(JPanel chart) {
        try {
            if (data == null || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No chart data available to export.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("financial_pie_chart_report.pdf"));
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File outFile = chooser.getSelectedFile();

            // Render chart to image
            int w = Math.max(chart.getWidth(), 800);
            int h = Math.max(chart.getHeight(), 600);
            BufferedImage chartImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = chartImage.createGraphics();
            chart.paint(g2);
            g2.dispose();

            // Save temporary image
            File tempImage = new File("chart_temp.png");
            ImageIO.write(chartImage, "png", tempImage);

            // Export via PDFExporter (matching financial report structure)
            PDFExporter.exportPieChart(outFile, tempImage, data, reportType);

            JOptionPane.showMessageDialog(this,
                    "✅ Pie chart exported successfully!\nSaved at: " + outFile.getAbsolutePath());

            tempImage.delete(); // cleanup
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error exporting pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------ Inner class for drawing chart ------------------
    private class ChartArea extends JPanel {
        private Shape[] sliceShapes;
        private double[] startAngles;

        public ChartArea() {
            setPreferredSize(new Dimension(700, 500));
            setOpaque(false);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    checkHover(e.getPoint());
                }
            });
        }

        private void checkHover(Point p) {
            int prev = hoveredSlice;
            hoveredSlice = -1;
            if (sliceShapes != null) {
                for (int i = 0; i < sliceShapes.length; i++) {
                    if (sliceShapes[i] != null && sliceShapes[i].contains(p)) {
                        hoveredSlice = i;
                        break;
                    }
                }
            }
            if (hoveredSlice != prev) repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 180;
            int cx = width / 2;
            int cy = height / 2 - 20;

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            double curAngle = 0.0;

            Color[] colors = {
                new Color(121, 134, 203),
                new Color(244, 143, 177),
                new Color(129, 199, 132),
                new Color(255, 213, 79),
                new Color(100, 181, 246),
                new Color(206, 147, 216)
            };

            sliceShapes = new Shape[data.size()];
            startAngles = new double[data.size()];

            // Drop shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillOval(cx - diameter / 2 + 6, cy - diameter / 2 + 6, diameter, diameter);

            int colorIndex = 0;
            int sliceIndex = 0;

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue();
                double angle = value / total * 360.0;
                Color base = colors[colorIndex % colors.length];

                double expansion = (hoveredSlice == sliceIndex) ? 15 : 0;

                Arc2D.Double arc = new Arc2D.Double(
                        cx - diameter / 2 - expansion / 2,
                        cy - diameter / 2 - expansion / 2,
                        diameter + expansion, diameter + expansion,
                        curAngle, angle, Arc2D.PIE);

                GradientPaint gp = new GradientPaint(
                        cx, cy - diameter / 2, base.brighter(),
                        cx, cy + diameter / 2, base.darker());
                g2.setPaint(gp);
                g2.fill(arc);

                sliceShapes[sliceIndex] = arc;
                startAngles[sliceIndex] = curAngle;

                // Label
                double midAngle = Math.toRadians(curAngle + angle / 2);
                double outerX = cx + Math.cos(midAngle) * (diameter / 2 + 25);
                double outerY = cy + Math.sin(midAngle) * (diameter / 2 + 25);

                String label = String.format("%.1f%%", value / total * 100);
                FontMetrics fm = g2.getFontMetrics();
                int lw = fm.stringWidth(label);

                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect((int) (outerX - lw / 2 - 4), (int) (outerY - fm.getAscent()),
                        lw + 8, fm.getHeight(), 8, 8);

                g2.setColor(Color.DARK_GRAY);
                g2.drawString(label, (int) (outerX - lw / 2), (int) outerY);

                curAngle += angle;
                colorIndex++;
                sliceIndex++;
            }

            drawLegend(g2, width, height, colors);
        }

        private void drawLegend(Graphics2D g2, int width, int height, Color[] colors) {
            int boxSize = 16;
            int startX = width / 2 - 100;
            int startY = height - 70;
            int spacing = 24;
            int i = 0;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                Color color = colors[i % colors.length];
                g2.setColor(color);
                g2.fillRect(startX, startY, boxSize, boxSize);
                g2.setColor(new Color(80, 80, 80));
                g2.drawRect(startX, startY, boxSize, boxSize);
                g2.drawString(entry.getKey(), startX + boxSize + 8, startY + 13);
                startY += spacing;
                i++;
            }
        }
    }
}
