package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Room;

import javax.swing.JComboBox;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

public static List<Room> getAllRooms() {
    List<Room> rooms = new ArrayList<>();
    String sql = "SELECT room_id, name, capacity, status FROM rooms";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("room_id");
            String name = rs.getString("name");
            int capacity = rs.getInt("capacity");
            String status = rs.getString("status");

            rooms.add(new Room(id, name, capacity, status));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return rooms;
}
public static void populateComboBox(javax.swing.JComboBox<Room> comboBox) {
    comboBox.removeAllItems();
    comboBox.addItem(new Room(0, "Select event room", 0, "Available"));

    List<Room> rooms = getAllRooms();
    for (Room room : rooms) {
        comboBox.addItem(room);
    }
}





    // Get a single room by ID
    public static Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("room_id"),
                        rs.getString("name"),
                        rs.getInt("capacity"), rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching room: " + e.getMessage());
        }
        return null;
    }

    // Create a new room
    public static boolean createRoom(Room room) {
        String sql = "INSERT INTO rooms (name, capacity) VALUES (?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating room: " + e.getMessage());
            return false;
        }
    }

    // Update an existing room
    public static boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET name = ?, capacity = ? WHERE room_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setInt(3, room.getRoomId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }

    // Delete a room by ID
    public static boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }
    public static void releaseExpiredRooms() {
    String sql = """
        UPDATE rooms r
        JOIN events e ON r.room_id = e.room_id
        SET r.status = 'Available'
        WHERE e.event_date < NOW()
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        int updated = ps.executeUpdate();
        System.out.println("✅ Rooms freed automatically: " + updated);
    } catch (SQLException e) {
        System.err.println("Error releasing expired rooms: " + e.getMessage());
    }
}
public static void markRoomAsAvailable(int roomId) {
    String sql = "UPDATE rooms SET status = 'Available' WHERE room_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, roomId);
        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error freeing room: " + e.getMessage());
    }
}
public static boolean isRoomAvailable(int roomId, java.util.Date eventDate) {
    String sql = "SELECT COUNT(*) AS count FROM events WHERE room_id = ? AND DATE(event_date) = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, roomId);
        ps.setDate(2, new java.sql.Date(eventDate.getTime()));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int count = rs.getInt("count");
            return count == 0; // no event exists on that date
        }
    } catch (SQLException e) {
        System.err.println("Error checking room availability: " + e.getMessage());
    }
    return false; // default to unavailable if error
}


// Mark a room as booked
public static void markRoomAsBooked(int roomId) {
    String sql = "UPDATE rooms SET status = 'Booked' WHERE room_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, roomId);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error marking room as booked: " + e.getMessage());
    }
}

public static List<Room> getAvailableRooms(java.util.Date eventDate) {
    List<Room> availableRooms = new ArrayList<>();
    String sql = "SELECT * FROM rooms WHERE status = 'Available' AND room_id NOT IN " +
                 "(SELECT room_id FROM events WHERE DATE(event_date) = ?)";
    
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, new java.sql.Date(eventDate.getTime()));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Room room = new Room(
                rs.getInt("room_id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                rs.getString("status")
            );
            availableRooms.add(room);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return availableRooms;
}



}
