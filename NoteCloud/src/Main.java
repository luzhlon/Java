import java.lang.reflect.Executable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.LinkedList;

/**
 * Created by John on 2015/6/7.
 */
public class Main {

   /* Test code
    public static void main(String[] args) {
        new MyHandler();
    }// */
    //*
    public static void main(String[] args) {
        ServerSocket ser;
        try {
            ser = new ServerSocket(4320, 5);
            while(true) {
                Socket sock = ser.accept();
                new ThreadConnect(sock, new MyHandler()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // */
}

class MyHandler extends MessageHandler {
    String username;
    int userid;
    //Database
    Connection con;
    Statement sql;
    ResultSet  rsl;
    DatabaseMetaData meta;

    MyHandler() {
        try {
            //Load the mysql jdbc driver
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            System.out.println("Load mysql.jdbc success.");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306", "luzhlon", "12345");
            sql = con.createStatement();
            meta = con.getMetaData();
            //Choose the database
            sql.executeQuery("use cloud");
            //Check tables
            boolean users = false, data = false;
            rsl = sql.executeQuery("SHOW TABLES");
            while(rsl.next()) {
                switch (rsl.getString(1)) {
                    case "users":
                        users = true;
                        break;
                    case "data":
                        data = true;
                        break;
                }
            }
            if(!users) { //users table is not exist
                System.out.println("create table users");
                sql.execute("CREATE TABLE users(" +
                        "id INT(11) NOT NULL AUTO_INCREMENT," +
                        "username VARCHAR(16) UNIQUE," +
                        "password VARCHAR(32)," +
                        "birthday DATE," +
                        "sex ENUM('boy','girl')," +
                        "PRIMARY KEY (id)" +
                        ") DEFAULT CHARSET=utf8;");
            }
            if(!data) { //data table is not exist
                System.out.println("create table data");
                sql.execute("CREATE TABLE data(" +
                        "user_id INT(11)," +
                        "note_name VARCHAR(64) UNIQUE," +
                        "time_create VARCHAR(20)" +
                        "time_modify VARCHAR(20)" +
                        "note_content TEXT," +
                        "CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES users(id)" +
                        ") DEFAULT CHARSET=utf8;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //'signin' username password
    //-> 'signin' {'yes' userinfo || 'no' error}
    void handle_signin(LinkedList<Object> args) {
        String user = (String) args.get(1);
        String passwd = (String) args.get(2);
        try {
            PreparedStatement pst =
                    con.prepareStatement("SELECT id " +
                            "FROM users WHERE username=? AND password=?");
            pst.setString(1, user);
            pst.setString(2, passwd);
            rsl = pst.executeQuery();
            if(rsl.next()) { //如果查询到结果,则登录成功
                args.set(1, "yes");
                this.username = user;
                this.userid = rsl.getInt(1);
                System.out.println("New user signin:" + username);
                return;
            } else {
                args.set(1, "no");
                args.set(2, "用户名或密码不正确");
            }
        } catch (Exception e) {
            e.printStackTrace();
            args.set(1, "no");
        }
    }
    //'signup' username password
    //-> 'yes' || 'no' error
    void handle_signup(LinkedList<Object> args) {
        try {
            String user = (String) args.get(1);
            String passwd = (String) args.get(2);

            PreparedStatement pst =
                    con.prepareStatement("SELECT id FROM users WHERE username=?");
            pst.setString(1, user);
            rsl = pst.executeQuery();
            if(rsl.next()) { //查询到结果表明该用户已经存在
                args.set(1, "no");
                args.set(2, "该用户名已经注册");
                return;
            }
            pst = con.prepareStatement("INSERT INTO users" +
                            "(username, password) VALUES (?,?)");
            pst.setString(1, user);
            pst.setString(2, passwd);
            pst.executeUpdate();
            args.set(1, "yes");
            System.out.println("New user signup:" + user);
        } catch (Exception e) {
            e.printStackTrace();
            args.set(1, "no");
            args.set(2, e.toString());
        }
    }

    //'push_note' note_name create_time modify_time note_content
    //-> 'yes' || 'no' error
    void handle_push_note(LinkedList<Object> args) {
        try {
            String name = (String)args.get(1);
            String create = (String)args.get(2);
            String modify = (String)args.get(3);
            String content = (String)args.get(4);

            PreparedStatement pst =
                    con.prepareStatement("SELECT user_id " +
                            "FROM data WHERE note_name=? AND user_id=?");
            pst.setString(1, name);
            pst.setInt(2, userid);
            rsl = pst.executeQuery();
            String state;
            if(rsl.next()) { //查询到结果
                state = "UPDATE data SET "+
                        "time_create=?," +
                        "time_modify=?," +
                        "note_content=? WHERE note_name=? AND user_id=?";
                pst = con.prepareStatement(state);
                pst.setString(1, create);
                pst.setString(2, modify);
                pst.setString(3, content);
                pst.setString(4, name);
                pst.setInt(5, userid);
                System.out.println(username + " updated " + name + ":" + content);
            } else {
                state = "INSERT INTO " +
                        "data(user_id,note_name,time_create," +
                        "time_modify,note_content) VALUES(?,?,?,?,?)";
                pst = con.prepareStatement(state);
                pst.setInt(1, userid);
                pst.setString(2, name);
                pst.setString(3, create);
                pst.setString(4, modify);
                pst.setString(5, content);
                System.out.println(username + " added " + name + ":" + content);
            }
            pst.executeUpdate();
            args.set(1, "yes");
        } catch (Exception e) {
            e.printStackTrace();
            args.set(1, "no");
            args.set(2, e.toString());
        }
    }
    //'note_list'
    //-> 'note_list' 'yes' | 'no' note_name1 note_name2 ...
    void handle_note_list(LinkedList<Object> args) {
        args.clear();
        args.add("note_list");
        try {
            rsl = sql.executeQuery("SELECT note_name FROM data " +
                    "WHERE user_id=" + userid);
            String note_name;
            args.add("yes");
            while (rsl.next()) {
                note_name = rsl.getString(1);
                args.add(note_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            args.add(1, "no");
            args.add(2, e.toString());
        }
    }
    //'pull_note' [note_name]
    //->'pull_note' 'yes' | 'no' note_content of note_name
    void handle_pull_note(LinkedList<Object> args) {
        try { //get all notes
            String name = (String) args.get(1);
            PreparedStatement pst =
                    con.prepareStatement("SELECT note_content " +
                            "FROM data WHERE note_name=? AND user_id=?");
            pst.setString(1, name);
            pst.setInt(2, userid);
            rsl = pst.executeQuery();
            rsl.next();
            args.set(1, "yes");
            String content = rsl.getString(1);
            args.add(2, content);
        } catch (Exception e) {
            e.printStackTrace();
            args.set(1, "no");
            args.add(2, e.toString());
        }
    }
    //'pull_all_note'
    //->'pull_all_note' {all_note}
    void handle_pull_all_note(LinkedList<Object> args) {
        args.clear();
        args.add("pull_all_note");
        try { //get all notes
            PreparedStatement pst =
                    con.prepareStatement("SELECT note_name," +
                            "time_create,time_modify,note_content " +
                            "FROM data WHERE user_id=?");
            pst.setInt(1, this.userid);
            rsl = pst.executeQuery();
            args.add("yes");
            while (rsl.next()) {
                args.add(rsl.getString(1));
                args.add(rsl.getString(2));
                args.add(rsl.getString(3));
                args.add(rsl.getString(4));
            }
            System.out.println(this.username + " got all notes.");
        } catch (Exception e) {
            e.printStackTrace();
            args.add("no");
            args.add(e.toString());
        }
    }

    //'delete_note'
    void handle_delete_note(LinkedList<Object> args) {
        try {
            String name = (String) args.get(1);
            PreparedStatement pst =
                    con.prepareStatement("DELETE FROM DATA " +
                            "WHERE note_name=? AND user_id=?");
            pst.setString(1, name);
            pst.setInt(2, userid);
            pst.executeUpdate();
            System.out.println(username + " deleted " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void handle_heartbeat(LinkedList<Object> args) {
        args.set(1, "yes");
    }
}
