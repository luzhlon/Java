package server;

import java.sql.*;

/**
 * 
 */
public class DB {
    private static Connection conn = null;

    /**
     * 
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        boolean needConnect = false;
        if(conn == null) needConnect = true;
        else if(conn.isClosed()) needConnect = true;
        if(needConnect) {
            Class.forName("com.mysql.jdbc.Driver");
            String user = "abc";
            String passwd = "12345";
            String db = "work";
            String host = "127.0.0.1";
            String port = "3306";
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
            conn = DriverManager.getConnection(url, user, passwd);
        }
        return conn;
    }

    /**
     * 
     * @return
     */
    public static Statement getStatement(){
        try{
            Connection c = getConnection();
            return c.createStatement();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    // 
    public static PreparedStatement preStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean preExecute(String fsql, Object... args) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(fsql);
            for (int i = 0; i < args.length; i++) {
            	ps.setObject(i+1, args[i]);
            }
            ps.execute();
        	return true;
        } catch (SQLException e) {
			e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	return false;
    }
    public static ResultSet preQuery(String fsql, Object... args) {
        try {
            PreparedStatement ps = getConnection().prepareStatement(fsql);
            for (int i = 0; i < args.length; i++) {
            	ps.setObject(i+1, args[i]);
            }
            ResultSet rs = ps.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // 
    public static ResultSet executeQuery(String sql){
        ResultSet set=null;
        try{
            Statement state=getStatement();
            set = state.executeQuery(sql);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return set;
    }
    // 
    public static boolean existTable(String table) {
        try {
            getStatement().executeQuery("DESC " + table);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    // 
    public static boolean execute(String sql){
        try{
            Statement state = getStatement();
            return state.execute(sql);
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    // 
    public static int getRowCount(String table) { return getRowCount(table, null); }
    public static int getRowCount(String table, String condition) {
        int count = -1;
        String sql = "SELECT COUNT(*) FROM " + table;
        if(condition != null)
            sql += condition;
        try {
            PreparedStatement ps = DB.preStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next());
                count = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}