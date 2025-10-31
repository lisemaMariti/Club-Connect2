package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // Get attendance for a specific event
    public static List<Attendance> getAttendanceForEvent(int eventId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.event_id, a.user_id, a.check_in_time, a.status, u.name AS user_name " +
                     "FROM attendance a " +
                     "JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.event_id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Attendance a = new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("event_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("check_in_time"),
                        rs.getString("status")
                );
                a.setUserName(rs.getString("user_name"));
                list.add(a);
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
}
