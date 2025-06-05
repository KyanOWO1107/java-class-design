package dao;

import java.sql.SQLException;
import java.util.List;

public interface CategoryDAO {
    List<String> getAllCategories() throws SQLException;
    void addCategory(String name) throws SQLException;
    void updateCategory(String oldName, String newName) throws SQLException;
    void deleteCategory(String name) throws SQLException;
    List<Object[]> getAllCategoriesWithID() throws SQLException;
    List<String> searchCategories(String keyword) throws SQLException;
}
