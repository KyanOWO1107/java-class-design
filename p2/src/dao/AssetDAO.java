package dao;

import entity.Asset;
import java.sql.SQLException;
import java.util.List;

public interface AssetDAO {
    void deleteAsset(String assetId) throws SQLException;  // Add this line
    void borrowAsset(String assetId, String operator) throws SQLException;
    void returnAsset(String assetId) throws SQLException; 
    void scrapAsset(String assetId) throws SQLException;
    void addAsset(Asset asset) throws SQLException;
    void updateAsset(Asset asset) throws SQLException;
    List<Asset> getAllAssets() throws SQLException;
    List<Object[]> getAllOperations() throws SQLException;
    
    List<Asset> searchAssets(String keyword) throws SQLException;
}

// Remove the concrete class implementation from this file
// and move it to a separate AssetDAOImpl.java file