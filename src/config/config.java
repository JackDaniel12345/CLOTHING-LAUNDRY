package config;

import java.sql.*;
import java.util.*;
import java.security.MessageDigest;

public class config {

    private final String DB_URL = "jdbc:sqlite:clothing.db";

    // ===========================
    // CONNECT TO DATABASE
    // ===========================
    private Connection connectDB() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }

    // ===========================
    // CREATE, ALTER, DROP TABLES
    // ===========================
    public void execute(String sql) {
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Execute Error: " + e.getMessage());
        }
    }

    // ===========================
    // INSERT RECORD
    // ===========================
    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++)
                pstmt.setObject(i + 1, values[i]);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    // ===========================
    // FETCH RECORDS
    // ===========================
    public List<Map<String, Object>> fetchRecords(String sql, Object... values) {

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++)
                pstmt.setObject(i + 1, values[i]);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int colCount = rs.getMetaData().getColumnCount();

                for (int c = 1; c <= colCount; c++) {
                    row.put(rs.getMetaData().getColumnName(c), rs.getObject(c));
                }

                list.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Fetch Error: " + e.getMessage());
        }

        return list;
    }

    // ===========================
    // UPDATE RECORD
    // ===========================
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++)
                pstmt.setObject(i + 1, values[i]);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    // ===========================
    // HASH PASSWORD (SHA-256)
    // ===========================
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : hash)
                hex.append(String.format("%02x", b));

            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
