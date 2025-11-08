/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import static clubconnect.dao.RoomDAO.markRoomAsAvailable;
import clubconnect.db.DBManager;
import clubconnect.models.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class EventDAO {

public static int createEvent(Event event, int clubId) {
    String sql = "INSERT INTO events (club_id, name, description, event_date, room_id) VALUES (?, ?, ?, ?, ?)";
    String updateRoomStatus = "UPDATE rooms SET status = 'Booked' WHERE room_id = ?";
    try (Connection conn = DBManager.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, clubId);
            ps.setString(2, event.getName());
            ps.setString(3, event.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(event.getEventDate().getTime()));
            ps.setInt(5, event.getRoomId());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int eventId = keys.getInt(1);

                try (PreparedStatement ps2 = conn.prepareStatement(updateRoomStatus)) {
                    ps2.setInt(1, event.getRoomId());
                    ps2.executeUpdate();
                }

                conn.commit();
                return eventId;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return -1; // failed
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
    public static List<Event> getEventsByLeaderId(int leaderId) {
    List<Event> events = new ArrayList<>();
    String sql = """
        SELECT e.* 
        FROM events e
        JOIN clubs c ON e.club_id = c.club_id
        WHERE c.leader_id = ?
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, leaderId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            events.add(new Event(
                    rs.getInt("event_id"),
                    rs.getInt("club_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("event_date"),
                    rs.getInt("room_id")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error fetching events by leader ID: " + e.getMessage());
    }

    return events;
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


public static boolean deleteEvent(int eventId) {
    String getRoomSql = "SELECT room_id FROM events WHERE event_id = ?";
    String deleteEventSql = "DELETE FROM events WHERE event_id = ?";

    try (Connection conn = DBManager.getConnection()) {
        conn.setAutoCommit(false);

        int roomId = -1;
        try (PreparedStatement ps = conn.prepareStatement(getRoomSql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) roomId = rs.getInt("room_id");
        }

        try (PreparedStatement ps = conn.prepareStatement(deleteEventSql)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        }

        // Free the room if found
        if (roomId > 0) {
            markRoomAsAvailable(roomId);
        }

        conn.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    // Returns all events for a specific club
    public static List<Event> getAllEvents(int clubId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE club_id = ? ORDER BY event_date";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Date eventDate = rs.getDate("event_date"); // java.sql.Date
                int roomId = rs.getInt("room_id");

                // Convert java.sql.Date to java.util.Date
                java.util.Date utilDate = new java.util.Date(eventDate.getTime());

                Event event = new Event(eventId, clubId, name, description, utilDate, roomId);
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching events: " + e.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return events;
    }
    
   public static List<Event> getUpcomingEvents() {
    List<Event> events = new ArrayList<>();

    String sql = """
        SELECT 
            e.event_id,
            e.name AS event_name,
            e.description,
            e.event_date,
            c.name AS club_name,
            r.name AS room_name,
            SUM(CASE WHEN rsvp_status = 'Yes' THEN 1 ELSE 0 END) AS yes_count,
            SUM(CASE WHEN rsvp_status = 'Maybe' THEN 1 ELSE 0 END) AS maybe_count,
            SUM(CASE WHEN rsvp_status = 'Waitlist' THEN 1 ELSE 0 END) AS waitlist_count
        FROM events e
        LEFT JOIN clubs c ON e.club_id = c.club_id
        LEFT JOIN rooms r ON e.room_id = r.room_id
        LEFT JOIN event_rsvps rsvp ON e.event_id = rsvp.event_id
        WHERE e.event_date >= NOW()
        GROUP BY e.event_id, e.name, e.description, e.event_date, c.name, r.name
        ORDER BY e.event_date ASC
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Event event = new Event();
            event.setEventId(rs.getInt("event_id"));
            event.setName(rs.getString("event_name"));
            event.setDescription(rs.getString("description"));
            event.setEventDate(rs.getTimestamp("event_date"));
            event.setClubName(rs.getString("club_name"));
            event.setRoomName(rs.getString("room_name"));

            // Enhanced RSVP info
            event.setYesRsvpCount(rs.getInt("yes_count"));
            event.setMaybeRsvpCount(rs.getInt("maybe_count"));
            event.setWaitlistCount(rs.getInt("waitlist_count"));

            events.add(event);
        }

    } catch (SQLException e) {
        System.err.println("Error fetching upcoming events: " + e.getMessage());
    }

    return events;
}
   
   public static Event getEventById(int eventId) {
    String query = "SELECT event_id, club_id, name, description, event_date, room_id FROM events WHERE event_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Event ev = new Event(
                rs.getInt("event_id"),
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("event_date"),
                rs.getInt("room_id")
            );
            return ev;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}



}




