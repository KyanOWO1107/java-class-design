package DataOperator;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
 
/**连接数据库操作的类*/
public class Database {
	private Statement stmt = null;
	ResultSet rs = null;
	private Connection conn = null;
	String url = "jdbc:mysql://localhost:3306/test1";   //数据库链接
	//构造函数
	public Database() {
		
	}
	/**打开数据库连接*/
	public void OpenConn() throws Exception {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url,"root","123456");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 执行查询语句返回结果集rs
	 */
	public ResultSet QueryInfo(String sql) {
		stmt = null;
		rs = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
		} catch (Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
		return rs;
	}
	public int RecordNumber(String sql) {
		stmt = null;
		rs = null;
		int row = 0;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			if(rs.last()) {
				row = rs.getRow();
			}
		} catch(Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
		return row;
	}
	public int getMaxId(String sql) throws Exception {
		stmt = null;
		rs = null;
		int number = -1;
		try{
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				if(number < rs.getInt(1)) {
					number = rs.getInt(1);
				}
			}
		} catch(Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
		return number;
	}
	public int getMinId(String sql) throws Exception {
		stmt = null;
		rs = null;
		int number = 100000;
		try{
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				if(number > rs.getInt(1)) {
					number = rs.getInt(1);
				}
			}
		} catch(Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
		return number;
	}
	/**执行更新语句*/
	public void UpdateInfo(String sql) {
		stmt = null;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			stmt.executeUpdate(sql);
		} catch(Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
	}
	/**关闭Statement*/
	public void closeStmt() {
		try{
			stmt.close();
		} catch(Exception ex) {
			System.err.println("executeQuery:" + ex.getMessage());
		}
	}
	/**关闭数据库连接*/
	public void closeConn() {
		try{
			conn.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}