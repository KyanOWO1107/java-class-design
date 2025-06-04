

public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public boolean addEmployee(Employee emp) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // 主表插入
            String empSql = "INSERT INTO employee(id,name,gender,birthdate,department_id,salary) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(empSql)) {
                pstmt.setString(1, emp.getId());
                pstmt.setString(2, emp.getName());
                pstmt.setString(3, emp.getGender());
                pstmt.setDate(4, new java.sql.Date(emp.getBirthdate().getTime()));
                pstmt.setInt(5, emp.getDepartmentId());
                pstmt.setDouble(6, emp.getSalary());
                pstmt.executeUpdate();
            }

            // 日志记录（补充完整字段）
            String logSql = "INSERT INTO operation_log(employee_id,operation_type,old_data,new_data) VALUES(?,?,?,?)";
            try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
                logStmt.setString(1, emp.getId());
                logStmt.setString(2, "新增员工");
                logStmt.setString(3, "");  // 旧数据为空
                logStmt.setString(4, emp.toString()); // 新数据为员工完整信息
                logStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            // 增强错误处理
            try {
                if (conn != null) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "事务回滚：" + e.getMessage());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
}