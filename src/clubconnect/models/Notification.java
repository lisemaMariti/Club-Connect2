package clubconnect.models;

import java.sql.Timestamp;

public class Notification {
    private int notificationId;
    private int userId;
    private String message;
    private Timestamp sentAt;
    private int clubId;

    // ✅ Optional: support linking to RSVP (useful for event-based notifications)
    private Integer rsvpId;  

    public Notification(int notificationId, int userId, String message, Timestamp sentAt, int clubId) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.sentAt = sentAt;
        this.clubId = clubId;
    }

    // ✅ Overloaded constructor for convenience
    public Notification(int userId, String message, int clubId) {
        this(0, userId, message, null, clubId);
    }

    // ✅ Overloaded constructor if linked to RSVP
    public Notification(int userId, String message, int clubId, Integer rsvpId) {
        this(0, userId, message, null, clubId);
        this.rsvpId = rsvpId;
    }

    // ----- Getters & Setters -----
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    // ✅ New optional RSVP field
    public Integer getRsvpId() { return rsvpId; }
    public void setRsvpId(Integer rsvpId) { this.rsvpId = rsvpId; }
}
