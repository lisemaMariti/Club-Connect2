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

    public static boolean createEvent(Event event) {
        String sql = "INSERT INTO events (club_id, name, description, event_date, room_id, capacity) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, event.getClubId());
            ps.setString(2, event.getName());
            ps.setString(3, event.getDescription());
            ps.setTimestamp(4, new Timestamp(event.getEventDate().getTime()));
            ps.setInt(5, event.getRoomId());
            ps.setInt(6, event.getCapacity());
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
                        rs.getInt("room_id"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
        return list;
    }
}
