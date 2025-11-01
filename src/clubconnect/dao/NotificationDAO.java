package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // ✅ Existing methods (unchanged)
    public static boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, club_id, message, sent_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, notification.getUserId());
            ps.setInt(2, notification.getClubId());
            ps.setString(3, notification.getMessage());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
        }
        return false;
    }

    public static List<Notification> getNotificationsByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY sent_at DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getTimestamp("sent_at"),
                        rs.getInt("club_id")
                );
                notifications.add(n);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching notifications: " + e.getMessage());
        }

        return notifications;
    }

    public static boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    public static List<Notification> getNotificationsForMember(int memberId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT n.notification_id, n.user_id, n.club_id, n.message, n.sent_at " +
                     "FROM notifications n " +
                     "JOIN memberships m ON n.club_id = m.club_id " +
                     "WHERE m.user_id = ? " +
                     "ORDER BY n.sent_at DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getTimestamp("sent_at"),
                        rs.getInt("club_id")
                );
                notifications.add(n);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching notifications for member: " + e.getMessage());
        }

        return notifications;
    }

    // ============================================================
    // ✅ NEW ADDITIONS (below this line)
    // ============================================================

    /**
     * Add a notification linked to a specific RSVP.
     * This keeps your existing notifications separate and just extends functionality.
     */public static boolean createWaitlistNotification(int rsvpId) {
    String sql = "INSERT INTO event_waitlist_notifications (rsvp_id, notified_at) VALUES (?, NOW())";

    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, rsvpId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error creating waitlist notification: " + e.getMessage());
        return false;
    }
}


    /**
     * Retrieve notifications linked to a specific RSVP.
     */
    public static List<Notification> getNotificationsByRSVP(int rsvpId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE rsvp_id = ? ORDER BY sent_at DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rsvpId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getTimestamp("sent_at"),
                        rs.getInt("club_id")
                );
                n.setRsvpId(rsvpId);
                notifications.add(n);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching RSVP notifications: " + e.getMessage());
        }

        return notifications;
    }
    public static boolean createRSVPNotification(int userId, int clubId, int rsvpId, String message) {
    String sql = "INSERT INTO notifications (user_id, club_id, rsvp_id, message, sent_at) VALUES (?, ?, ?, ?, NOW())";
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setInt(2, clubId);
        ps.setInt(3, rsvpId);
        ps.setString(4, message);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error creating RSVP-linked notification: " + e.getMessage());
        return false;
    }
}

}
