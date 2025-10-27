/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.BudgetRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public static boolean submitBudgetRequest(BudgetRequest br) {
        String sql = "INSERT INTO budget_requests (event_id, amount, status, purpose) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, br.getEventId());
            ps.setDouble(2, br.getAmount());
            ps.setString(3, br.getStatus());
            ps.setString(4, br.getPurpose());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error submitting budget request: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBudgetStatus(int budgetId, String status) {
        String sql = "UPDATE budget_requests SET status=? WHERE budget_id=?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, budgetId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating budget request: " + e.getMessage());
            return false;
        }
    }

    public static List<BudgetRequest> getAllBudgetRequests() {
        List<BudgetRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM budget_requests";
        try (Connection conn = DBManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BudgetRequest(
                        rs.getInt("budget_id"),
                        rs.getInt("event_id"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getString("purpose")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading budget requests: " + e.getMessage());
        }
        return list;
    }
}

