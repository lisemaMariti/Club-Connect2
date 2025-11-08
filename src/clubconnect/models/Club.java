package clubconnect.models;

public class Club {
    private int clubId;
    private String name;
    private String description;
    private String status;
    private int leaderId;
    private String leaderName; // optional
    private int memberCount;
    private int upcomingEvents;
   

    public Club() {}

    // Constructor with metrics
    public Club(int clubId, String name, String description, String status, int leaderId,
                int memberCount, int upcomingEvents) {
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.leaderId = leaderId;
        this.memberCount = memberCount;
        this.upcomingEvents = upcomingEvents;
    }

    // Constructor without metrics
    public Club(int clubId, String name, String description, String status, int leaderId) {
        this(clubId, name, description, status, leaderId, 0, 0);
    }

    // --- Getters & Setters ---
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

    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getUpcomingEvents() { return upcomingEvents; }
    public void setUpcomingEvents(int upcomingEvents) { this.upcomingEvents = upcomingEvents; }

    // --- Helper Methods ---
    public boolean isApproved() {
        return "Approved".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
}
