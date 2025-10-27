/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

import java.sql.Timestamp;

public class Membership {
    private int membershipId;
    private int userId;
    private int clubId;
    private String status;
    private Timestamp joinedAt;

    public Membership(int membershipId, int userId, int clubId, String status, Timestamp joinedAt) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.clubId = clubId;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public Membership(int userId, int clubId, String status) {
        this(0, userId, clubId, status, null);
    }

    public int getMembershipId() { return membershipId; }
    public void setMembershipId(int membershipId) { this.membershipId = membershipId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
}

