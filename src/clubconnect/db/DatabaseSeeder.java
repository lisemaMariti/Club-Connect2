package clubconnect.db;

import clubconnect.dao.UserDAO;
import clubconnect.models.User;
import clubconnect.utils.PasswordUtil;

import java.sql.*;

/**
 * Seeds initial data into the database (e.g., default admin account).
 */
public class DatabaseSeeder {

    public static void seedAdminUser() {
        String checkQuery = "SELECT * FROM users WHERE role = 'Admin' LIMIT 1";
        String insertQuery = "INSERT INTO users (name, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             ResultSet rs = checkStmt.executeQuery()) {

            // ✅ Check if an admin already exists
            if (rs.next()) {
                System.out.println("✅ Admin account already exists: " + rs.getString("email"));
                return;
            }

            // ✅ No admin found — create default admin
            String defaultAdminName = "System Admin";
            String defaultAdminEmail = "admin@clubconnect.com";
            String defaultPassword = "Admin123"; // You can change this later
            String hashedPassword = PasswordUtil.hashPassword(defaultPassword);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, defaultAdminName);
                insertStmt.setString(2, defaultAdminEmail);
                insertStmt.setString(3, hashedPassword);
                insertStmt.setString(4, "Admin");
                insertStmt.setBoolean(5, true);
                insertStmt.executeUpdate();

                System.out.println("✅ Default admin user created successfully!");
                System.out.println("📧 Email: " + defaultAdminEmail);
                System.out.println("🔑 Password: " + defaultPassword);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error seeding admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
