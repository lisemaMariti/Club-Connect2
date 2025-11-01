/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Club;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    public static boolean createClub(Club club) {
        String sql = "INSERT INTO clubs (name, description, status, leader_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, club.getName());
            ps.setString(2, club.getDescription());
            ps.setString(3, club.getStatus());
            ps.setInt(4, club.getLeaderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating club: " + e.getMessage());
            return false;
        }
    }

    public static List<Club> getAllClubs() {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT * FROM clubs";
        try (Connection conn = DBManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Club(
                    rs.getInt("club_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getInt("leader_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading clubs: " + e.getMessage());
        }
        return list;
    }

    public static boolean updateStatus(int clubId, String newStatus) {
        String sql = "UPDATE clubs SET status = ? WHERE club_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, clubId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating club status: " + e.getMessage());
            return false;
        }
    }
    
    public static Club getClubByLeaderId(int leaderId) {
    String sql = "SELECT * FROM clubs WHERE leader_id=?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, leaderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Club(
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getInt("leader_id")
            );
        }
    } catch (SQLException e) {
        System.err.println("Error fetching leader club: " + e.getMessage());
    }
    return null;
}

    public static boolean doesLeaderHaveClub(int leaderId) {
    String sql = "SELECT COUNT(*) FROM clubs WHERE leader_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, leaderId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error checking leader club: " + e.getMessage());
    }
    return false;
}
public static boolean hasPendingClubs() {
    String sql = "SELECT COUNT(*) FROM clubs WHERE status = 'Pending'";
    try (Connection conn = DBManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        System.err.println("Error checking pending clubs: " + e.getMessage());
    }
    return false;
}

public static boolean approveClub(int clubId) {
    String sql = "UPDATE clubs SET status = 'Active' WHERE club_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, clubId);
        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error approving club: " + e.getMessage());
        return false;
    }
}

public static int getClubIdByLeader(int leaderId) {
    String query = "SELECT club_id FROM clubs WHERE leader_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setInt(1, leaderId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("club_id");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0; 
}

public static Club getClubById(int clubId) {
        String sql = "SELECT * FROM clubs WHERE club_id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Using the parameterized constructor
                    return new Club(
                        rs.getInt("club_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getInt("leader_id")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching club by ID: " + e.getMessage());
        }

        return null; // Return null if no club found or on error
    }

public static Club getClubByEventId(int eventId) {
    String sql = "SELECT c.* FROM clubs c " +
                 "JOIN events e ON c.club_id = e.club_id " +
                 "WHERE e.event_id = ?";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, eventId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Club(
                    rs.getInt("club_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getInt("leader_id")
                );
            }
        }

    } catch (SQLException e) {
        System.err.println("Error fetching club by event ID: " + e.getMessage());
    }

    return null; // no club found
}


}

