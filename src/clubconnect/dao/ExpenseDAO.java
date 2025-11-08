/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.dao;

import clubconnect.db.DBManager;
import clubconnect.models.Expense;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public static List<Expense> getExpensesByBudget(int budgetId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE budget_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, budgetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("expense_id"),
                        rs.getInt("budget_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getDate("expense_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public static boolean addExpense(Expense exp) {
        String sql = "INSERT INTO expenses (budget_id, description, amount, expense_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, exp.getBudgetId());
            ps.setString(2, exp.getDescription());
            ps.setDouble(3, exp.getAmount());
            ps.setDate(4, new java.sql.Date(exp.getExpenseDate().getTime()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

