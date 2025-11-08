package clubconnect.ui;

import javax.swing.*;
import java.awt.*;
import clubconnect.models.BudgetRequest;

public class BudgetRequestDialog extends JDialog {

    private int clubId;
    private int eventId;

    private JTextField txtAmount;
    private JTextArea txtPurpose;
    private JButton btnSubmit, btnCancel;

    private BudgetRequest budgetRequest = null; // Store the result

    public BudgetRequestDialog(Frame parent, int clubId, int eventId) {
        super(parent, "Request Budget Allocation", true);
        this.clubId = clubId;
        this.eventId = eventId;

        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JLabel lblAmount = new JLabel("Estimated Amount (R):");
        txtAmount = new JTextField(10);

        JLabel lblPurpose = new JLabel("Purpose / Description:");
        txtPurpose = new JTextArea(4, 20);
        txtPurpose.setLineWrap(true);
        txtPurpose.setWrapStyleWord(true);
        JScrollPane purposeScroll = new JScrollPane(txtPurpose);

        btnSubmit = new JButton("Submit Request");
        btnCancel = new JButton("Cancel");

        btnSubmit.addActionListener(e -> submitRequest());
        btnCancel.addActionListener(e -> {
            budgetRequest = null; // No request
            dispose();
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblAmount, gbc);
        gbc.gridx = 1;
        panel.add(txtAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblPurpose, gbc);
        gbc.gridx = 1;
        panel.add(purposeScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnSubmit, gbc);
        gbc.gridy = 3;
        panel.add(btnCancel, gbc);

        add(panel);
        pack();
    }

    private void submitRequest() {
        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String purpose = txtPurpose.getText().trim();

            if (purpose.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide a purpose.");
                return;
            }

            // ✅ Use the correct constructor for a new budget request
            budgetRequest = new BudgetRequest(clubId, eventId, amount, purpose);

            // Close the dialog after creating the request
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
        }
    }

    /**
     * Returns the BudgetRequest created by the dialog, or null if cancelled
     */
    public BudgetRequest getBudgetRequest() {
        return budgetRequest;
    }
}
