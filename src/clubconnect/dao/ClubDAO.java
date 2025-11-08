/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Club;
import clubconnect.models.User;
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
 String sql = "SELECT c.*, \n" +
"       (SELECT COUNT(*) FROM memberships m WHERE m.club_id = c.club_id) AS memberCount,\n" +
"       (SELECT COUNT(*) FROM events e WHERE e.club_id = c.club_id AND e.event_date >= CURDATE()) AS upcomingEvents\n" +
"FROM clubs c;";

    try (Connection conn = DBManager.getConnection();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {

        while (rs.next()) {
            Club club = new Club(
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getInt("leader_id")
            );
            club.setMemberCount(rs.getInt("memberCount"));
            club.setUpcomingEvents(rs.getInt("upcomingEvents"));
            list.add(club);
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
    String sql = "UPDATE clubs SET status = 'Approved' WHERE club_id = ?";
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

public static boolean updateClubProfile(Club club) {
    String sql = "UPDATE clubs SET name = ?, description = ? WHERE club_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, club.getName());
        ps.setString(2, club.getDescription());
        ps.setInt(3, club.getClubId());
        
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error updating club profile: " + e.getMessage());
        return false;
    }


}
public static boolean deactivateClub(int clubId) {
    String sql = "UPDATE clubs SET status = 'Rejected' WHERE club_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, clubId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error deactivating club: " + e.getMessage());
        return false;
    }
}
public static List<Club> searchClubs(String keyword, String status) {
    List<Club> clubs = new ArrayList<>();
    String sql = "SELECT * FROM clubs WHERE 1=1";

    if (keyword != null && !keyword.isEmpty()) {
        sql += " AND (name LIKE ? OR description LIKE ?)";
    }

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        if (keyword != null && !keyword.isEmpty()) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            clubs.add(new Club(
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getInt("leader_id")
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return clubs;
}


public static List<Club> getAllClubsWithMetrics() {
    List<Club> clubs = new ArrayList<>();
    String sql = "SELECT c.club_id, c.name, c.description, c.status, c.leader_id, " +
                 "(SELECT COUNT(*) FROM club_members WHERE club_id = c.club_id) AS member_count, " +
                 "(SELECT COUNT(*) FROM events WHERE club_id = c.club_id AND event_date >= NOW()) AS upcoming_events " +
                 "FROM clubs c";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            clubs.add(new Club(
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getInt("leader_id"),
                rs.getInt("member_count"),
                rs.getInt("upcoming_events")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error fetching clubs with metrics: " + e.getMessage());
    }

    return clubs;
}


public static List<Club> getApprovedClubs() {
    List<Club> clubs = new ArrayList<>();

    String sql = """
        SELECT c.club_id, 
               c.name, 
               c.description, 
               c.status,
               c.leader_id,
               u.name AS leader_name
        FROM clubs c
        LEFT JOIN users u ON c.leader_id = u.user_id
        WHERE c.status = 'Approved'
        ORDER BY c.name ASC
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Club club = new Club();
            club.setClubId(rs.getInt("club_id"));
            club.setName(rs.getString("name"));
            club.setDescription(rs.getString("description"));
            club.setStatus(rs.getString("status"));
            club.setLeaderId(rs.getInt("leader_id"));
            club.setLeaderName(rs.getString("leader_name")); // ✅ sets leader name
            clubs.add(club);
        }

    } catch (SQLException e) {
        System.err.println("❌ Error fetching approved clubs: " + e.getMessage());
    }

    return clubs;
}


public static String getClubName(int clubId) {
    String name = "Your Club";
    String sql = "SELECT name FROM clubs WHERE club_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, clubId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) name = rs.getString("name");
    } catch (Exception e) {
        e.printStackTrace();
    }
    return name;
}

 public static List<Club> getClubsForUser(int userId) {
        List<Club> clubs = new ArrayList<>();
        String sql = """
            SELECT c.club_id, c.name, m.status AS membership_status
            FROM memberships m
            JOIN clubs c ON m.club_id = c.club_id
            WHERE m.user_id = ?
        """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Club club = new Club();
                club.setClubId(rs.getInt("club_id"));
                club.setName(rs.getString("name"));
                club.setStatus(rs.getString("membership_status")); // membership status
                clubs.add(club);
            }

        } catch (SQLException e) {
            System.err.println("Error loading user clubs: " + e.getMessage());
        }

        return clubs;
    }





}

