/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.ui;

import clubconnect.dao.ExpenseDAO;
import clubconnect.models.Expense;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;

public class BudgetExpenseDialog extends JDialog {

    private int budgetId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtDescription, txtAmount;

    public BudgetExpenseDialog(Frame parent, int budgetId) {
        super(parent, "Budget Expenses", true);
        this.budgetId = budgetId;

        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);

        // Table model
        tableModel = new DefaultTableModel(new String[]{"ID", "Description", "Amount", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // Set preferred height for table scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(480, 200)); // table height ~200px
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for adding new expense
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtDescription = new JTextField(12);
        txtAmount = new JTextField(6);
        JButton btnAdd = new JButton("Add Expense");
        panel.add(new JLabel("Description:"));
        panel.add(txtDescription);
        panel.add(new JLabel("Amount:"));
        panel.add(txtAmount);
        panel.add(btnAdd);
        add(panel, BorderLayout.SOUTH);

        // Load expenses
        loadExpenses();

        // Add expense action
        btnAdd.addActionListener(e -> {
            String desc = txtDescription.getText().trim();
            double amt;
            try {
                amt = Double.parseDouble(txtAmount.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount");
                return;
            }
            Expense exp = new Expense(0, budgetId, desc, amt, new Date());
            if (ExpenseDAO.addExpense(exp)) {
                JOptionPane.showMessageDialog(this, "Expense added");
                loadExpenses();
                txtDescription.setText("");
                txtAmount.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense");
            }
        });
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);
        List<Expense> expenses = ExpenseDAO.getExpensesByBudget(budgetId);
        for (Expense exp : expenses) {
            tableModel.addRow(new Object[]{
                    exp.getExpenseId(),
                    exp.getDescription(),
                    exp.getAmount(),
                    exp.getExpenseDate()
            });
        }
    }
}
