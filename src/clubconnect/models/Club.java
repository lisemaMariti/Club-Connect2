/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

public class Club {
    private int clubId;
    private String name;
    private String description;
    private String status;
    private int leaderId;

    public Club(int clubId, String name, String description, String status, int leaderId) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.leaderId = leaderId;
    }

    // Getters and setters
    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getLeaderId() { return leaderId; }
    public void setLeaderId(int leaderId) { this.leaderId = leaderId; }
}
