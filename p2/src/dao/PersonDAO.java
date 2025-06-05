package dao;

import java.sql.SQLException;
import java.util.List;

public interface PersonDAO {
    void delete(String staffId) throws SQLException;
    List<Object[]> getAll() throws SQLException;
    void update(String staffId, String name, String department, String position, String phone) throws SQLException;
    
    // 新增添加方法
    void add(String staffId, String name, String department, String position, String phone) throws SQLException;
    
    // Add this new method declaration
    List<Object[]> searchPersons(String keyword) throws SQLException;
}