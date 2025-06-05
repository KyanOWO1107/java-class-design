package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBUtil;
import entity.Person;

public class PersonDAOImpl implements PersonDAO {
    // Remove @Override annotation from this method since it's not in the interface
    public void add(Person person) throws SQLException {
        String sql = "INSERT INTO staff (staff_id, name, department, position, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, person.getStaffId());
            ps.setString(2, person.getName());
            ps.setString(3, person.getDepartment());
            ps.setString(4, person.getPosition());
            ps.setString(5, person.getPhone());
            ps.executeUpdate();
        }
    }

    @Override
    public void add(String staffId, String name, String department, String position, String phone) throws SQLException {
        Person person = new Person();
        person.setStaffId(staffId);
        person.setName(name);
        person.setDepartment(department);
        person.setPosition(position);
        person.setPhone(phone);
        add(person); // Reuse the existing add(Person) method
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
    public void delete(String staffId) throws SQLException {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId);
            ps.executeUpdate();
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