/*
 * BudgetDAO.java
 * Handles all CRUD operations for budgets and budget requests
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.BudgetRequest;
import clubconnect.models.Notification;
import java.sql.*;
import java.util.*;

public class BudgetDAO {

    // ==============================
    // Submit a new budget request (Leader)
    // ==============================
  public static boolean submitBudgetRequest(BudgetRequest br) {
    String sql = """
            INSERT INTO budgets (club_id, event_id, description, amount, status, created_at)
            VALUES (?, ?, ?, ?, 'Pending', NOW())
            """;
    try (Connection conn = DBManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, br.getClubId());
        if (br.getEventId() > 0) {
            ps.setInt(2, br.getEventId());
        } else {
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        ps.setString(3, br.getPurpose());
        ps.setDouble(4, br.getAmount());

        int rows = ps.executeUpdate();
        if (rows > 0) {
            System.out.println("✅ Budget request submitted successfully for club_id=" + br.getClubId());
            return true;
        }
    } catch (SQLException e) {
        System.err.println("❌ Error submitting budget request: " + e.getMessage());
    }
    return false;
}


    // ==============================
    // Update budget status (Admin)
    // ==============================
    public static boolean updateBudgetStatus(int budgetId, String status) {
        String sql = "UPDATE budgets SET status = ? WHERE budget_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, budgetId);

            boolean updated = ps.executeUpdate() > 0;

            if (updated) {
                // Send notification to the club leader
                sendBudgetStatusNotification(budgetId, status);
            }

            return updated;
        } catch (SQLException e) {
            System.err.println("❌ Error updating budget status: " + e.getMessage());
            return false;
        }
    }

    // ==============================
    // Helper: Notify leader when budget is approved/rejected
    // ==============================
    private static void sendBudgetStatusNotification(int budgetId, String status) {
        try (Connection conn = DBManager.getConnection()) {
            String query = """
                    SELECT b.description, c.leader_id, c.name AS club_name
                    FROM budgets b
                    JOIN clubs c ON b.club_id = c.club_id
                    WHERE b.budget_id = ?
                    """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, budgetId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int leaderId = rs.getInt("leader_id");
                String description = rs.getString("description");
                String clubName = rs.getString("club_name");

                String message = String.format(
                        "Your budget request ('%s') for club '%s' has been %s by the Admin.",
                        description, clubName, status.toLowerCase()
                );

                String insert = "INSERT INTO notifications (user_id, message, sent_at) VALUES (?, ?, NOW())";
                PreparedStatement ps2 = conn.prepareStatement(insert);
                ps2.setInt(1, leaderId);
                ps2.setString(2, message);
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("⚠️ Failed to send budget notification: " + e.getMessage());
        }
    }

    // ==============================
    // Fetch a single budget request by ID
    // ==============================
    public static BudgetRequest getBudgetById(int budgetId) {
        String sql = "SELECT * FROM budgets WHERE budget_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new BudgetRequest(
                        rs.getInt("budget_id"),
                        rs.getInt("club_id"),
                        rs.getInt("event_id"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching budget by ID: " + e.getMessage());
        }
        return null;
    }

    // ==============================
    // Load all budget requests (Admin)
    // ==============================
    public static List<BudgetRequest> getAllBudgetRequests() {
        List<BudgetRequest> list = new ArrayList<>();
        String sql = """
                SELECT b.*, c.name AS club_name, e.name AS event_name
                FROM budgets b
                LEFT JOIN clubs c ON b.club_id = c.club_id
                LEFT JOIN events e ON b.event_id = e.event_id
                ORDER BY b.created_at DESC
                """;

        try (Connection conn = DBManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                BudgetRequest b = new BudgetRequest(
                        rs.getInt("budget_id"),
                        rs.getInt("club_id"),
                        rs.getInt("event_id"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("description")
                );
                b.setClubName(rs.getString("club_name"));
                b.setEventName(rs.getString("event_name"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching all budget requests: " + e.getMessage());
        }
        return list;
    }

    // ==============================
    // Get budgets for a specific club (Leader view)
    // ==============================
    public static List<BudgetRequest> getBudgetsByClub(int clubId) {
        List<BudgetRequest> list = new ArrayList<>();
        String sql = """
                SELECT b.*, e.name AS event_name
                FROM budgets b
                LEFT JOIN events e ON b.event_id = e.event_id
                WHERE b.club_id = ?
                ORDER BY b.created_at DESC
                """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clubId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BudgetRequest b = new BudgetRequest(
                        rs.getInt("budget_id"),
                        rs.getInt("club_id"),
                        rs.getInt("event_id"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("description")
                );
                b.setEventName(rs.getString("event_name"));
                list.add(b);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading club budgets: " + e.getMessage());
        }
        return list;
    }

    // ==============================
    // Financial Summary for Reports
    // ==============================
    public static List<Map<String, Object>> getFinancialSummary() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
                SELECT c.name AS club_name,
                       COUNT(b.budget_id) AS total_requests,
                       SUM(CASE WHEN b.status = 'Approved' THEN b.amount ELSE 0 END) AS approved_total,
                       SUM(CASE WHEN b.status = 'Pending' THEN b.amount ELSE 0 END) AS pending_total,
                       SUM(CASE WHEN b.status = 'Rejected' THEN b.amount ELSE 0 END) AS rejected_total
                FROM budgets b
                JOIN clubs c ON b.club_id = c.club_id
                GROUP BY c.name
                ORDER BY c.name
                """;

        try (Connection conn = DBManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("club_name", rs.getString("club_name"));
                row.put("total_requests", rs.getInt("total_requests"));
                row.put("approved_total", rs.getDouble("approved_total"));
                row.put("pending_total", rs.getDouble("pending_total"));
                row.put("rejected_total", rs.getDouble("rejected_total"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error generating financial summary: " + e.getMessage());
        }
        return list;
    }

    // ==============================
    // Delete budget request (optional cleanup)
    // ==============================
    public static boolean deleteBudgetRequest(int budgetId) {
        String sql = "DELETE FROM budgets WHERE budget_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting budget: " + e.getMessage());
            return false;
        }
    }
    
}
