package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Attendance;
import clubconnect.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

// Get RSVPs for a specific event
public static List<User> getAttendeesForEvent(int eventId) {
    List<User> list = new ArrayList<>();
    String sql = "SELECT u.user_id, u.name, u.email, r.rsvp_status " +
                 "FROM event_rsvps r " +
                 "JOIN users u ON r.user_id = u.user_id " +
                 "WHERE r.event_id = ? AND r.rsvp_status IN ('Yes', 'Maybe', 'No')";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
//            user.setRsvpStatus(rs.getString("rsvp_status"));
            list.add(user);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}


    // Mark attendance
    public static boolean markAttendance(int eventId, int userId, boolean present) {
        String sqlCheck = "SELECT * FROM attendance WHERE event_id = ? AND user_id = ?";
        String sqlInsert = "INSERT INTO attendance (event_id, user_id, check_in_time, status) VALUES (?, ?, ?, ?)";
        String sqlUpdate = "UPDATE attendance SET check_in_time = ?, status = ? WHERE event_id = ? AND user_id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

            psCheck.setInt(1, eventId);
            psCheck.setInt(2, userId);
            ResultSet rs = psCheck.executeQuery();

            Timestamp now = new Timestamp(System.currentTimeMillis());
            String status = present ? "Present" : "Absent";

            if (rs.next()) {
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setTimestamp(1, now);
                    psUpdate.setString(2, status);
                    psUpdate.setInt(3, eventId);
                    psUpdate.setInt(4, userId);
                    return psUpdate.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    psInsert.setInt(1, eventId);
                    psInsert.setInt(2, userId);
                    psInsert.setTimestamp(3, now);
                    psInsert.setString(4, status);
                    return psInsert.executeUpdate() > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  public static Map<String, Integer> getAttendanceCounts() {
    Map<String, Integer> data = new LinkedHashMap<>();
    String sql = "SELECT e.name AS event_name, COUNT(a.user_id) AS total_attendees " +
                 "FROM events e " +
                 "LEFT JOIN attendance a ON e.event_id = a.event_id AND a.status = 'Present' " +
                 "GROUP BY e.name " +
                 "ORDER BY e.event_date";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (!rs.isBeforeFirst()) {
            System.out.println("ResultSet is empty!");
        }

        while (rs.next()) {
            String name = rs.getString("event_name");
            int count = rs.getInt("total_attendees");
            System.out.println("Event: " + name + ", Count: " + count); // <-- debug print
            data.put(name, count);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}

    

}
