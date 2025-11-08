/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Club;
import clubconnect.models.User;
import clubconnect.utils.EmailUtil;
import clubconnect.utils.PasswordUtil;
import clubconnect.utils.TokenUtil;
import static clubconnect.utils.TokenUtil.hashToken;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author User
 */
public class UserDAO {
 private static User currentUser = null;


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


  // --- LOGIN ---
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
                    System.out.println("⚠ User inactive: " + email);
                    return null;
                }

                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            isActive
                    );

                    currentUser = user; // store session
                    System.out.println("✅ Logged in: " + user.getName() + " (" + user.getRole() + ")");
                    return user;
                } else {
                    System.out.println("⚠ Invalid password for: " + email);
                }
            } else {
                System.out.println("⚠ No user found with email: " + email);
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return null;
    }
    // --- LOGOUT ---
    public static void logout() {
        if (currentUser != null) {
            System.out.println("🔒 Logging out user: " + currentUser.getName() + " (ID: " + currentUser.getUserId() + ")");
        } else {
            System.out.println("⚠ No user currently logged in.");
        }
        currentUser = null; // clear session
    }

    // --- SESSION HELPERS ---
    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT u.user_id, u.name, u.email, u.password_hash, u.role, u.is_active
            FROM users u
            ORDER BY u.name ASC
        """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("role"),
                    rs.getBoolean("is_active")
                );

                // Fetch user's clubs
                user.setClubs(ClubDAO.getClubsForUser(user.getUserId()));

                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }

        return users;
    }

public static List<User> loadUsers() {
    String sql = """
        SELECT u.user_id, u.name, u.email, u.role, u.is_active,
               c.name AS club_name,
               COALESCE(m.status, 'Leader') AS membership_status
        FROM users u
        LEFT JOIN memberships m ON u.user_id = m.user_id
        LEFT JOIN clubs c ON m.club_id = c.club_id OR u.user_id = c.leader_id
        ORDER BY u.name
    """;

    Map<Integer, User> userMap = new LinkedHashMap<>();

    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int userId = rs.getInt("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = new User(
                    userId,
                    rs.getString("name"),
                    rs.getString("email"),
                    null, // passwordHash not needed for display
                    rs.getString("role"),
                    rs.getBoolean("is_active")
                );
                userMap.put(userId, user);
            }

            String clubName = rs.getString("club_name");
            String membershipStatus = rs.getString("membership_status");
            if (clubName != null) {
                Club club = new Club();
                club.setName(clubName);
                club.setStatus(membershipStatus);
                user.getClubs().add(club);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return new ArrayList<>(userMap.values());
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
    String sql = """
        SELECT u.user_id, u.name, u.email, u.role, u.is_active,
               c.club_id, c.name AS club_name, 
               COALESCE(m.status, 'Leader') AS membership_status
        FROM users u
        LEFT JOIN memberships m ON u.user_id = m.user_id
        LEFT JOIN clubs c ON m.club_id = c.club_id OR u.user_id = c.leader_id
        WHERE CAST(u.user_id AS CHAR) LIKE ? 
           OR u.name LIKE ? 
           OR u.email LIKE ? 
           OR u.role LIKE ? 
           OR c.name LIKE ?
        ORDER BY u.name
    """;

    Map<Integer, User> userMap = new LinkedHashMap<>();

    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String searchPattern = "%" + keyword + "%";
        for (int i = 1; i <= 5; i++) {
            stmt.setString(i, searchPattern);
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = new User(
                    userId,
                    rs.getString("name"),
                    rs.getString("email"),
                    null, // password not needed
                    rs.getString("role"),
                    rs.getBoolean("is_active")
                );
                userMap.put(userId, user);
            }

            String clubName = rs.getString("club_name");
            String membershipStatus = rs.getString("membership_status");
            if (clubName != null) {
                Club club = new Club();
                club.setName(clubName);
                club.setStatus(membershipStatus);
                user.getClubs().add(club);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return new ArrayList<>(userMap.values());
}



private static final int TOKEN_EXPIRATION_MINUTES = 10;


public static String generatePasswordResetToken(int userId) {
    // Clear any existing tokens for this user first
    String clearSql = "DELETE FROM tokens WHERE user_id = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(clearSql)) {
        ps.setInt(1, userId);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error clearing old tokens: " + e.getMessage());
    }

    // Generate new token
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[32];
    random.nextBytes(bytes);
    String plainToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    // Hash the token before storing (store hashed, return plain)
    String hashedToken = PasswordUtil.hashPassword(plainToken);

    // Set expiration (10 minutes from now)
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

    String sql = "INSERT INTO tokens (user_id, token_hash, expires_at) VALUES (?, ?, ?)";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setString(2, hashedToken); // Store hashed
        ps.setTimestamp(3, Timestamp.valueOf(expiresAt));

        int inserted = ps.executeUpdate();
        if (inserted > 0) {
            System.out.println("✅ Password reset token created for user_id: " + userId);
            System.out.println("🔐 DEBUG - Plain token being returned: " + plainToken);
            return plainToken; // Return plain token for email
        }
    } catch (SQLException e) {
        System.err.println("Error creating password reset token: " + e.getMessage());
    }

    return null;
}

public static boolean verifyResetToken(int userId, String inputToken) {
    String sql = "SELECT token_hash, is_used, expires_at FROM tokens WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("token_hash");
            boolean isUsed = rs.getBoolean("is_used");
            Timestamp expiresAt = rs.getTimestamp("expires_at");

            // DEBUG OUTPUT
            System.out.println("🔐 DEBUG VERIFICATION:");
            System.out.println("🔐 User ID: " + userId);
            System.out.println("🔐 Input token: " + inputToken);
            System.out.println("🔐 Input token length: " + inputToken.length());
            System.out.println("🔐 Stored hash: " + storedHash);
            System.out.println("🔐 Stored hash length: " + storedHash.length());
            System.out.println("🔐 Is used: " + isUsed);
            System.out.println("🔐 Expires at: " + expiresAt);
            System.out.println("🔐 Current time: " + new Timestamp(System.currentTimeMillis()));

            if (isUsed) {
                System.out.println("❌ Token already used");
                return false;
            }
            if (expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                System.out.println("❌ Token expired");
                return false;
            }

            // Test both verification methods
            boolean passwordUtilResult = PasswordUtil.verifyPassword(inputToken, storedHash);
            boolean tokenUtilResult = TokenUtil.verifyToken(inputToken, storedHash);
            
            System.out.println("🔐 PasswordUtil.verifyPassword result: " + passwordUtilResult);
            System.out.println("🔐 TokenUtil.verifyToken result: " + tokenUtilResult);
            
            return passwordUtilResult;
        } else {
            System.out.println("❌ No token found for user ID: " + userId);
        }

    } catch (SQLException e) {
        System.err.println("❌ Database error in verifyResetToken: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}
// Mark token as used
public static void markTokenAsUsed(int userId) {
    String sql = "UPDATE tokens SET is_used = 1 WHERE user_id = ? AND is_used = 0";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static boolean updatePasswordByEmail(String email, String hashedPassword) {
    String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
    
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, hashedPassword);
        ps.setString(2, email);

        int updated = ps.executeUpdate();
        System.out.println("🔐 DEBUG - Password update attempted for: " + email + ", rows affected: " + updated);
        return updated > 0;

    } catch (SQLException e) {
        System.err.println("❌ Error updating password: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public static boolean sendPasswordResetEmail(String email, int userId) {
    System.out.println("🔐 Starting token generation for user ID: " + userId);
    
    // Generate token for this user
    String plainToken = UserDAO.generatePasswordResetToken(userId); 
    if (plainToken == null) {
        System.err.println("❌ Failed to generate password reset token.");
        return false;
    }

    System.out.println("✅ Plain token generated: " + plainToken);
    System.out.println("✅ Token length: " + plainToken.length());

    String body = """
        Hello,

        You requested a password reset. Use the following token to reset your password:

        %s

        This token will expire in 10 minutes. Do not share it with anyone.

        If you did not request this, please ignore this email.

        Best regards,
        Your Team
        """.formatted(plainToken);

    try {
        System.out.println("📧 Attempting to send email to: " + email);
        EmailUtil.sendEmail(email, "Password Reset Token", body);
        System.out.println("✅ Password reset email sent to: " + email);
        return true;
    } catch (MessagingException e) {
        System.err.println("❌ Failed to send password reset email: " + e.getMessage());
        return false;
    }
}
public static int getUserIdByEmail(String email) {
    String sql = "SELECT user_id FROM users WHERE email = ?";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
    } catch (SQLException e) {
        System.err.println("Error getting user ID: " + e.getMessage());
    }
    return -1;
}

    public static boolean updateProfile(User user, boolean updatePassword) {
        String sql;

        if (updatePassword) {
            sql = "UPDATE users SET name = ?, email = ?, password_hash = ? WHERE user_id = ?";
        } else {
            sql = "UPDATE users SET name = ?, email = ? WHERE user_id = ?";
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());

            if (updatePassword) {
                ps.setString(3, user.getPasswordHash());
                ps.setInt(4, user.getUserId());
            } else {
                ps.setInt(3, user.getUserId());
            }

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

public static List<User> getUsersByClubId(int clubId) {
    List<User> members = new ArrayList<>();
    String sql = """
        SELECT u.* 
        FROM users u
        JOIN memberships m ON u.user_id = m.user_id
        WHERE m.club_id = ? AND m.status = 'Active' AND u.is_active = TRUE
          AND u.role = 'Member'
    """;

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, clubId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            User user = new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("is_active")
            );
            members.add(user);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return members;
}


    


}

    



