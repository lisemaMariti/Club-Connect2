/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Membership;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MembershipDAO {

public static boolean requestMembership(int userId, int clubId) {
    String checkSql = "SELECT * FROM memberships WHERE user_id = ? AND club_id = ?";
    String insertSql = "INSERT INTO memberships (user_id, club_id, status) VALUES (?, ?, 'Pending')";
    
    try (Connection conn = DBManager.getConnection()) {
        // Check for existing membership
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false; // Already applied
            }
        }

        // Insert new membership
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, clubId);
            return ps.executeUpdate() > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error requesting membership: " + e.getMessage());
        return false;
    }
}
public static boolean updateMembershipStatus(int membershipId, String status) {
    String sql = "UPDATE memberships SET status=? WHERE membership_id=?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, status);
        ps.setInt(2, membershipId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error updating membership: " + e.getMessage());
        return false;
    }
}


    public static List<Membership> getMembershipsByClub(int clubId) {
        List<Membership> list = new ArrayList<>();
        String sql = "SELECT * FROM memberships WHERE club_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Membership(
                        rs.getInt("membership_id"),
                        rs.getInt("user_id"),
                        rs.getInt("club_id"),
                        rs.getString("status"),
                        rs.getTimestamp("joined_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching memberships: " + e.getMessage());
        }
        return list;
    }


public static List<Membership> getPendingMembershipsByLeader(int leaderId) {
    List<Membership> list = new ArrayList<>();
    String sql = "SELECT * FROM memberships "
               + "WHERE status = 'Pending' AND club_id = "
               + "(SELECT club_id FROM clubs WHERE leader_id = ?)";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, leaderId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Membership m = new Membership(
                    rs.getInt("membership_id"),
                    rs.getInt("user_id"),
                    rs.getInt("club_id"),
                    rs.getString("status"),
                    rs.getTimestamp("joined_at")
                );
                list.add(m);
            }
        }

    } catch (SQLException e) {
        System.err.println("Error fetching pending memberships for leader: " + e.getMessage());
    }

    return list;
}
public static List<Map<String, Object>> getAttendanceSummary() {
    List<Map<String, Object>> list = new ArrayList<>();
    String sql = """
        SELECT 
            c.name AS club_name,
            e.name AS event_name,
            SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present_count,
            SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) AS absent_count
        FROM attendance a
        JOIN events e ON a.event_id = e.event_id
        JOIN clubs c ON e.club_id = c.club_id
        GROUP BY c.name, e.name
        ORDER BY c.name, e.name;
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Club", rs.getString("club_name"));
            row.put("Event", rs.getString("event_name"));
            row.put("Present", rs.getInt("present_count"));
            row.put("Absent", rs.getInt("absent_count"));
            list.add(row);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}








}
