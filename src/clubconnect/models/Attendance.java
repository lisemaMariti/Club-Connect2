package clubconnect.models;

import java.util.Date;

public class Attendance {
    private int attendanceId;
    private int eventId;
    private int userId;
    private Date checkInTime;
    private String status; 
    private String UserName;

    public Attendance() {}

    public Attendance(int attendanceId, int eventId, int userId, Date checkInTime, String status) {
        this.attendanceId = attendanceId;
        this.eventId = eventId;
        this.userId = userId;
        this.checkInTime = checkInTime;
        this.status = status;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
    
}
