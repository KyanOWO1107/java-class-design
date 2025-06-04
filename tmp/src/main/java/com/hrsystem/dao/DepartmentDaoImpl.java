public class DepartmentDaoImpl implements DepartmentDao {
    @Override
    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, name, parent_id, level FROM department";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("id"));
                dept.setName(rs.getString("name"));
                dept.setParentId(rs.getInt("parent_id"));
                dept.setLevel(rs.getInt("level"));
                departments.add(dept);
            }
        }
        return departments;
    }
}