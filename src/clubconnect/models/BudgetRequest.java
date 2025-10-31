/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

public class BudgetRequest {
    private int budgetId;
    private int clubId;
    private int eventId;
    private double amount;
    private String status;
    private String purpose;
    private String clubName;
    private String eventName;

    // --- Full constructor (used for database fetch) ---
    public BudgetRequest(int budgetId, int clubId, int eventId, double amount, String status, String purpose) {
        this.budgetId = budgetId;
        this.clubId = clubId;
        this.eventId = eventId;
        this.amount = amount;
        this.status = status;
        this.purpose = purpose;
    }

    // --- Simplified constructor for new requests (defaults to Pending) ---
    public BudgetRequest(int clubId, int eventId, double amount, String purpose) {
        this(0, clubId, eventId, amount, "Pending", purpose);
    }

    // --- Getters and Setters ---
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
}
