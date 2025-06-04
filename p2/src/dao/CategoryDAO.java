package dao;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.*;

public class CategoryDAO {
    private static final String TABLE = "asset_categories";

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM " + TABLE;
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(rs.getString(1));
            }
        }
        return categories;
    }

    public void addCategory(String name) throws SQLException {
        String sql = "INSERT INTO " + TABLE + "(category_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public void updateCategory(String oldName, String newName) throws SQLException {
        String sql = "UPDATE asset_categories SET category_name = ? WHERE category_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        }
    }

    public void deleteCategory(String name) throws SQLException {
        String sql = "DELETE FROM asset_categories WHERE category_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public List<Object[]> getAllCategoriesWithID() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM asset_categories";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{rs.getString(1), rs.getString(2)});
            }
        }
        return data;
    }
}
