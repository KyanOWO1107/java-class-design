package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBUtil;

public class PersonDAOImpl implements PersonDAO {
    @Override
    public void add(String staffId, String name, String dept, String position, String phone) throws SQLException {
        String sql = "INSERT INTO staff (staff_id, name, department, position, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            stmt.setString(2, name);
            stmt.setString(3, dept);
            stmt.setString(4, position);
            stmt.setString(5, phone);
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(String staffId) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM staff WHERE staff_id = ?")) {
            stmt.setString(1, staffId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(String staffId, String name, String department, String position, String phone) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE staff SET name = ?, department = ?, position = ?, phone = ? WHERE staff_id = ?")) {
             
            stmt.setString(1, name);
            stmt.setString(2, department);
            stmt.setString(3, position);
            stmt.setString(4, phone);
            stmt.setString(5, staffId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Object[]> getAll() throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT staff_id, name, department, position, phone FROM staff";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("staff_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getString("phone")
                };
                result.add(row);
            }
        }
        return result;
    }
}