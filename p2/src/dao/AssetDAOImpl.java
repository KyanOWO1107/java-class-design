package dao;

import entity.Asset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBUtil;

public class AssetDAOImpl implements AssetDAO {
    @Override
    public List<Asset> getAllAssets() throws SQLException {
        String sql = "SELECT * FROM assets";
        List<Asset> assets = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Asset asset = new Asset();
                asset.setAssetId(rs.getString("asset_id"));
                asset.setAssetName(rs.getString("asset_name"));
                asset.setType(rs.getString("category"));
                asset.setModel(rs.getString("model"));
                asset.setPrice(rs.getDouble("price"));
                asset.setPurchaseDate(rs.getDate("purchase_date"));
                asset.setStatus(rs.getInt("status"));
                asset.setRemark(rs.getString("remark"));
                assets.add(asset);
            }
        }
        return assets;
    }
    
    @Override
    public void addAsset(Asset asset) throws SQLException {
        String sql = "INSERT INTO assets (asset_id, asset_name, category, model, price, purchase_date, status, remark) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, asset.getAssetId());
            ps.setString(2, asset.getAssetName());
            ps.setString(3, asset.getType());
            ps.setString(4, asset.getModel());
            ps.setDouble(5, asset.getPrice());
            ps.setDate(6, new java.sql.Date(asset.getPurchaseDate().getTime()));
            ps.setInt(7, asset.getStatus());
            ps.setString(8, asset.getRemark());
            
            ps.executeUpdate();
        }
    }
    // Implement all interface methods here
    public void borrowAsset(String assetId, String operator) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            // 更新资产状态为1（已借出）
            String updateStatus = "UPDATE assets SET status=1 WHERE asset_id=?";
            try (PreparedStatement ps1 = conn.prepareStatement(updateStatus)) {
                ps1.setString(1, assetId);
                ps1.executeUpdate();
            }
            
            // 插入操作记录
            String insertOp = "INSERT INTO asset_operations (operation_type, asset_id, operator, operate_time) VALUES ('BORROW', ?, ?, NOW())";
            try (PreparedStatement ps2 = conn.prepareStatement(insertOp)) {
                ps2.setString(1, assetId); // 修改ps→ps2
                ps2.setString(2, operator); // 修改ps→ps2
                ps2.executeUpdate();
            }
        }
    }
    
    public void returnAsset(String assetId) throws SQLException {
        String sql = "UPDATE asset_operations SET operation_type='RETURN', operate_time=NOW() WHERE asset_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assetId);
            ps.executeUpdate();
        }
    }
    
    public void scrapAsset(String assetId) throws SQLException {
        String sql = "INSERT INTO asset_operations (operation_type, asset_id, operate_time) VALUES ('SCRAP', ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, assetId);
            ps.executeUpdate();
        }
    }
    
    // Keep existing addAsset/getAllAssets implementations
    @Override
    public void updateAsset(Asset asset) throws SQLException {
        String sql = "UPDATE assets SET asset_name=?, category=?, model=?, price=?, purchase_date=?, status=?, remark=? WHERE asset_id=?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, asset.getAssetName());
            ps.setString(2, asset.getType());
            ps.setString(3, asset.getModel());
            ps.setDouble(4, asset.getPrice());
            ps.setDate(5, new java.sql.Date(asset.getPurchaseDate().getTime()));
            ps.setInt(6, asset.getStatus());
            ps.setString(7, asset.getRemark());
            ps.setString(8, asset.getAssetId());
            
            ps.executeUpdate();
        }
    }
    
    @Override
    public void deleteAsset(String assetId) throws SQLException {
        String sql = "DELETE FROM assets WHERE asset_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) { // 使用try-with-resources
            ps.setString(1, assetId);
            ps.executeUpdate();
        }
    }
    
    @Override
    public List<Object[]> getAllOperations() throws SQLException {
        String sql = "SELECT operation_type, asset_id, operator, operate_time FROM asset_operations";
        List<Object[]> operations = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("operation_type"),
                    rs.getString("asset_id"),
                    rs.getString("operator"),
                    rs.getTimestamp("operate_time")
                };
                operations.add(row);
            }
        }
        return operations;
    }
    
    @Override
    public List<Asset> searchAssets(String keyword) throws SQLException {
        String sql = "SELECT * FROM assets WHERE asset_id LIKE ? OR asset_name LIKE ? OR category LIKE ? OR model LIKE ?";
        List<Asset> results = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, searchPattern);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Asset asset = new Asset();
                    asset.setAssetId(rs.getString("asset_id"));
                    asset.setAssetName(rs.getString("asset_name"));
                    asset.setType(rs.getString("category"));
                    asset.setModel(rs.getString("model"));
                    asset.setPrice(rs.getDouble("price"));
                    asset.setPurchaseDate(rs.getDate("purchase_date"));
                    asset.setStatus(rs.getInt("status"));
                    asset.setRemark(rs.getString("remark"));
                    results.add(asset);
                }
            }
        }
        return results;
    }
}