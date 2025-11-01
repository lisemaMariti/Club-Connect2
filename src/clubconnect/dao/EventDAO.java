/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

  public static boolean createEvent(Event event, int clubId) {
    String sql = "INSERT INTO events (club_id, name, description, event_date, room_id) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, clubId); 
        ps.setString(2, event.getName());
        ps.setString(3, event.getDescription());
        ps.setTimestamp(4, new Timestamp(event.getEventDate().getTime()));
        ps.setInt(5, event.getRoomId());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error creating event: " + e.getMessage());
        return false;
    }
}


    public static List<Event> getEventsByClub(int clubId) {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE club_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Event(
                        rs.getInt("event_id"),
                        rs.getInt("club_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getTimestamp("event_date"),
                        rs.getInt("room_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
        return list;
    }
    
    public static List<Event> getEventsByClubId(int clubId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.event_id, e.name, e.description, e.event_date, r.name AS room_name " +
                       "FROM events e " +
                       "JOIN rooms r ON e.room_id = r.room_id " +
                       "WHERE e.club_id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Event ev = new Event(
                    rs.getInt("event_id"),
                    clubId,
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("event_date"),
                    0 // roomId not needed here
                );
                ev.setRoomName(rs.getString("room_name")); 
                events.add(ev);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
    public static List<Event> getEventsForMember(int memberId) {
        List<Event> events = new ArrayList<>();
        
        String query = "SELECT e.event_id, e.name, e.description, e.event_date, r.name AS room_name, e.club_id " +
                       "FROM events e " +
                       "JOIN rooms r ON e.room_id = r.room_id " +
                       "JOIN memberships m ON e.club_id = m.club_id " +
                       "WHERE m.user_id = ? AND m.status = 'Active'";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Event ev = new Event(
                    rs.getInt("event_id"),
                    rs.getInt("club_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("event_date"),
                    0 // roomId not needed if only showing room name
                );
                ev.setRoomName(rs.getString("room_name"));
                events.add(ev);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
    
  public static boolean addRSVP(int eventId, int userId, String status, Integer waitlistPosition) {
    String sql = "INSERT INTO event_rsvps (event_id, user_id, rsvp_status, waitlist_position) " +
                 "VALUES (?, ?, ?, ?)";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, eventId);
        ps.setInt(2, userId);
        ps.setString(3, status); 
        if (waitlistPosition != null) {
            ps.setInt(4, waitlistPosition);
        } else {
            ps.setNull(4, java.sql.Types.INTEGER);
        }

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
  
  public static int getNextWaitlistPosition(int eventId) throws SQLException {
    String sql = "SELECT COUNT(*) AS cnt FROM event_rsvps WHERE event_id = ? AND rsvp_status = 'Waitlist'";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("cnt") + 1; // next available position
        }
    }
    return 1; // first
}
  
  public static int getClubIdByEvent(int eventId) throws SQLException {
    String sql = "SELECT club_id FROM events WHERE event_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("club_id");
    }
    return -1;
}
public static int addRSVPAndGetId(int eventId, int userId, String status, Integer waitlistPosition) throws SQLException {
    String sql = "INSERT INTO event_rsvps (event_id, user_id, rsvp_status, waitlist_position) VALUES (?, ?, ?, ?)";
    
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, eventId);
        ps.setInt(2, userId);
        ps.setString(3, status);

        if (waitlistPosition != null) {
            ps.setInt(4, waitlistPosition);
        } else {
            ps.setNull(4, java.sql.Types.INTEGER);
        }

        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating RSVP failed, no rows affected.");
        }

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1); // return generated RSVP ID
            } else {
                throw new SQLException("Creating RSVP failed, no ID obtained.");
            }
        }
    }
}
public static int getEventCapacity(int eventId) throws SQLException {
    String sql = "SELECT r.capacity " +
                 "FROM events e " +
                 "JOIN rooms r ON e.room_id = r.room_id " +
                 "WHERE e.event_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("capacity");
        }
    }
    return 0; // default if not found
}

public static int getConfirmedRSVPCount(int eventId) throws SQLException {
    String sql = "SELECT COUNT(*) AS count " +
                 "FROM event_rsvps " +
                 "WHERE event_id = ? AND rsvp_status = 'Yes'";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("count");
        }
    }
    return 0;
}




}
