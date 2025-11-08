/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;



import clubconnect.db.DBManager;
import java.sql.*;
import java.util.*;

public class ReportDAO {

    // Event Attendance Summary
    public static Map<String, Integer> getEventAttendanceSummary() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT e.event_name, COUNT(a.user_id) AS attendees " +
                     "FROM events e " +
                     "LEFT JOIN attendance a ON e.event_id = a.event_id AND a.status = 'Present' " +
                     "GROUP BY e.event_name " +
                     "ORDER BY e.event_date";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("event_name"), rs.getInt("attendees"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Club Engagement Metrics
    public static Map<String, Integer> getClubEngagement() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT c.club_name, COUNT(DISTINCT r.user_id) AS engaged_members " +
                     "FROM clubs c " +
                     "LEFT JOIN events e ON c.club_id = e.club_id " +
                     "LEFT JOIN event_rsvps r ON e.event_id = r.event_id " +
                     "GROUP BY c.club_name";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("club_name"), rs.getInt("engaged_members"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Budget Overview
    public static Map<String, Double> getBudgetOverview() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT e.event_name, COALESCE(SUM(f.amount),0) AS total_spent " +
                     "FROM events e " +
                     "LEFT JOIN finances f ON e.event_id = f.event_id " +
                     "GROUP BY e.event_name";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("event_name"), rs.getDouble("total_spent"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
