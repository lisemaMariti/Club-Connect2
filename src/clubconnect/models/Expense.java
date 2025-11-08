/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;
public class Expense {
    private int expenseId;
    private int budgetId;
    private String description;
    private double amount;
    private java.util.Date expenseDate;

    public Expense(int expenseId, int budgetId, String description, double amount, java.util.Date expenseDate) {
        this.expenseId = expenseId;
        this.budgetId = budgetId;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public int getExpenseId() { return expenseId; }
    public int getBudgetId() { return budgetId; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public java.util.Date getExpenseDate() { return expenseDate; }

    @Override
    public String toString() {
        return description + " - " + amount;
    }
}
