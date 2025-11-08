package clubconnect.models;

import java.util.Date;

public class Event {
    private int eventId;
    private int clubId;
    private String name;
    private String description;
    private Date eventDate;
    private int roomId;
    private String roomName;
    private String clubName;
    private int yesRsvpCount;
private int maybeRsvpCount;
private int waitlistCount;

    // Constructor
    public Event(int eventId, int clubId, String name, String description, Date eventDate, int roomId) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.roomId = roomId;
    }

    // Empty constructor (important for DAO use)
    public Event() {}

    // Getters/setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public int getYesRsvpCount() {
        return yesRsvpCount;
    }

    public void setYesRsvpCount(int yesRsvpCount) {
        this.yesRsvpCount = yesRsvpCount;
    }

    public int getMaybeRsvpCount() {
        return maybeRsvpCount;
    }

    public void setMaybeRsvpCount(int maybeRsvpCount) {
        this.maybeRsvpCount = maybeRsvpCount;
    }

    public int getWaitlistCount() {
        return waitlistCount;
    }

    public void setWaitlistCount(int waitlistCount) {
        this.waitlistCount = waitlistCount;
    }
    
}
