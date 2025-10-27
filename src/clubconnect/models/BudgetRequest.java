/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

public class BudgetRequest {
    private int budgetId;
    private int eventId;
    private double amount;
    private String status;
    private String purpose;

    public BudgetRequest(int budgetId, int eventId, double amount, String status, String purpose) {
        this.budgetId = budgetId;
        this.eventId = eventId;
        this.amount = amount;
        this.status = status;
        this.purpose = purpose;
    }

    public BudgetRequest(int eventId, double amount, String purpose) {
        this(0, eventId, amount, "Pending", purpose);
    }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
