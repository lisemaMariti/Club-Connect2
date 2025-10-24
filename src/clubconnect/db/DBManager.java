/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.db;
import java.sql.*;
import javax.swing.*;
import java.io.*;

/**
 *
 * @author User
 */
public class DBManager {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/clubconnect_db";
    private static final String USER = "root"; // change if your MySQL user differs
    private static final String PASS = "";     // or your MySQL password

    private static Connection conn;

    // Initialize connection
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Auto-create DB and tables if missing
    public static void initializeDatabase() {
    try {
        // Connect to MySQL without specifying a database
        Connection tempConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASS);
        Statement stmt = tempConn.createStatement();

        // Create DB if it doesn't exist
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS clubconnect_db");
        stmt.close();
        tempConn.close();

        // Now connect to the new DB
        conn = DriverManager.getConnection(DB_URL, USER, PASS);

        // Run SQL script to create tables
        File sqlFile = new File("sql/create_tables.sql");
        if (sqlFile.exists()) {
            String sql = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()));
            Statement st = conn.createStatement();
            for (String command : sql.split(";")) {
                if (!command.trim().isEmpty()) st.execute(command);
            }
            st.close();
            System.out.println("✅ Database and tables initialized successfully.");
        } else {
            System.err.println("❌ SQL script not found at: " + sqlFile.getAbsolutePath());
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage());
    }
}


    // Export database (runs on close)
    public static void exportDatabase() {
        try {
            Process p = Runtime.getRuntime().exec("mysqldump -u" + USER + " " + (PASS.isEmpty() ? "" : "-p" + PASS + " ") + "clubconnect_db -r clubconnect_backup.sql");
            p.waitFor();
            System.out.println("Database exported to clubconnect_backup.sql");
        } catch (Exception e) {
            System.err.println("Database export failed: " + e.getMessage());
        }
    }
    
}
