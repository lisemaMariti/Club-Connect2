/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.User;
import clubconnect.utils.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author User
 */
public class UserDAO {

    // Register a new user
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, PasswordUtil.hashPassword(user.getPasswordHash()));
            stmt.setString(4, user.getRole());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

// Login (authenticate)
public static User login(String email, String password) {
    String sql = "SELECT * FROM users WHERE email = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String storedHash = rs.getString("password_hash");
            boolean isActive = rs.getBoolean("is_active");

            if (!isActive) {
                // User is inactive, cannot login
                return null;
            }

            if (PasswordUtil.verifyPassword(password, storedHash)) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    isActive
                );
            }
        }
    } catch (SQLException e) {
        System.err.println("Login error: " + e.getMessage());
    }
    return null;
}

public static List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users";
    try (Connection conn = DBManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            users.add(new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("is_active") // <- make sure to read this column
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error loading users: " + e.getMessage());
    }
    return users;
}



public static boolean deleteUser(int userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        int affectedRows = stmt.executeUpdate();

        return affectedRows > 0; // returns true if a row was deleted

    } catch (SQLException e) {
        System.err.println("Error deleting user: " + e.getMessage());
        return false;
    }
}

public static boolean deactivateUser(int userId) {
    String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, userId);
        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error deactivating user: " + e.getMessage());
        return false;
    }
}

public static boolean assignUserRole(int userId, String newRole) {
    String sql = "UPDATE users SET role = ? WHERE user_id = ?";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, newRole);
        stmt.setInt(2, userId);

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error updating user role: " + e.getMessage());
        return false;
    }
}

public static List<User> searchUsers(String keyword) {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users WHERE " +
                 "CAST(user_id AS CHAR) LIKE ? OR " +
                 "name LIKE ? OR " +
                 "email LIKE ? OR " +
                 "role LIKE ?";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + keyword + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern);
        stmt.setString(4, searchPattern);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            users.add(new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("is_active")
            ));
        }

    } catch (SQLException e) {
        System.err.println("Error searching users: " + e.getMessage());
    }

    return users;
}


}
