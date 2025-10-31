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
        String query = "SELECT room_id, name, capacity FROM rooms";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("room_id");
                String name = rs.getString("name");
                int capacity = rs.getInt("capacity");
                rooms.add(new Room(id, name, capacity));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rooms;
    }

public static void populateComboBox(javax.swing.JComboBox comboBox) {
    comboBox.removeAllItems();
    comboBox.addItem(new Room(0, "Select event room", 0)); 
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
                        rs.getInt("capacity")
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
    
}
