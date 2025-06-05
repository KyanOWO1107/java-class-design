package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.DBUtil;

public class CategoryDAOImpl implements CategoryDAO {
    // private static final String TABLE = "asset_categories";

    @Override
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM asset_categories";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        }
        return categories;
    }

    @Override
    public void addCategory(String name) throws SQLException {
        String sql = "INSERT INTO asset_categories (category_name) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateCategory(String oldName, String newName) throws SQLException {
        String sql = "UPDATE asset_categories SET category_name = ? WHERE category_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteCategory(String name) throws SQLException {
        String sql = "DELETE FROM asset_categories WHERE category_name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    @Override
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

    @Override
    public List<String> searchCategories(String keyword) throws SQLException {
        List<String> results = new ArrayList<>();
        String sql = "SELECT category_name FROM asset_categories WHERE category_name LIKE ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("category_name"));
                }
            }
        }
        return results;
    }
}