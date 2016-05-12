package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Message.Message;
import Message.MessageHandler;

public class MyHandler extends MessageHandler {
    final int NULL = 0;
    final int TEACHER = 1;
    final int ADMIN = 1;
    int state = NULL;
    
	public void handle_login(Message msg) {
        ResultSet rs;
        Object id = msg.args[0];
        if (id instanceof Integer) {
        	state = TEACHER;
            rs = DB.preQuery(
        		"SELECT passwd FROM teacher WHERE id=?", id);
        } else {
        	state = ADMIN;
            rs = DB.preQuery(
        		"SELECT passwd FROM admin WHERE name=?", id);
        }
        try {
			if (!rs.next()) {
				msg.name = "err_id";
                return;
			}
            String passwd = (String) msg.args[1];
            if (!passwd.equals(rs.getString(1))) {
            	msg.name = "err_passwd";
            	return;
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    Integer getclassid(String cn) {
    	ResultSet rs = DB.preQuery(
    			"SELECT id FROM class WHERE name=?", cn);
    	try {
            if (rs.next()) {
				return rs.getInt(1);
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return -1;
    }
    Integer getbookid(String bn) {
    	ResultSet rs = DB.preQuery(
    			"SELECT id FROM book WHERE name=?", bn);
    	try {
            if (rs.next()) {
				return rs.getInt(1);
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return -1;
    }
    void delbuy(Message m) {
    	Integer i = (Integer) m.args[0];
        DB.preExecute("DELETE FROM tobuy WHERE id=?", i);
    }
    public void handle_delbuy(Message m) {
        delbuy(m);
    }
    public void handle_pushbuy(Message m) {
    	Integer i = (Integer) m.args[0];
    	ResultSet rs = DB.preQuery("SELECT book.count, class.count, book.id" +
    	    					" FROM tobuy,book,class WHERE tobuy.bookid=book.id" +
    	    					" and tobuy.id=? and tobuy.clsid=class.id", i);
    	if (rs == null)
    		return;
        try {
			if (rs.next()) {
                Integer bcount = rs.getInt(1);
                Integer ccount = rs.getInt(2);
                Integer bid = rs.getInt(3);
                if (bcount < ccount) return;	// ¿â´æ²»×ã
                
                DB.preExecute("UPDATE book SET `count`=? WHERE id=?", bcount - ccount, bid);
                
                delbuy(m);
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        m.name = "err";
    }
    // add book-buy request
	public void handle_addbuy(Message m) {
        String b = (String) m.args[0];
        String c = (String) m.args[1];
        if (!DB.preExecute(
        		"INSERT INTO tobuy (bookid, clsid) VALUES(?,?)",
        		getbookid(b), getclassid(c)))
        	m.name = "err";
	}
	public void handle_addbook(Message m) {
		String book = (String) m.args[0];
		PreparedStatement ps = DB.preStatement("INSERT INTO book (name) VALUES(?)");
		try {
			ps.setString(1, book);
			ps.execute();
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		m.name = "err";
		}
	}
	public void handle_booklist(Message m) {
        ResultSet rs = DB.executeQuery("SELECT name FROM book");
        if (rs == null) {
            m.name = "err";
        	return;
        }
        ArrayList<String> books = new ArrayList<>();
        try {
        	while (rs.next()) {
        		books.add(rs.getString(1));
        	}
        	m.args = books.toArray(new String[0]);
        } catch (SQLException e) {
        	e.printStackTrace();
            m.name = "err";
        }
	}
    public void handle_bookcount(Message m) {
    	ResultSet rs = DB.executeQuery("SELECT name, count FROM book");
    	ArrayList<Object> arr = new ArrayList<>();
    	try {
			while (rs.next()) {
				arr.add(rs.getString(1));
				arr.add(rs.getInt(2));
			}
            m.args = arr.toArray(new Object[0]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void handle_addbookc(Message m) {		// add book count
    	String book = (String) m.args[0];
    	Integer count = (Integer) m.args[1];
    	DB.preExecute(
    			"UPDATE book SET `count`=`count`+? WHERE name=?",
    			count, book);
    }
	public void handle_classlist(Message m) {
        ResultSet rs = DB.executeQuery("SELECT name FROM class");
        if (rs == null) {
            m.name = "err";
        	return;
        }
        ArrayList<String> classes = new ArrayList<>();
        try {
        	while (rs.next()) {
        		classes.add(rs.getString(1));
        	}
        	m.args = classes.toArray(new String[0]);
        } catch (SQLException e) {
        	e.printStackTrace();
            m.name = "err";
        }
	}
    public void handle_buylist(Message m) {
    	ResultSet rs = DB.executeQuery("SELECT tobuy.id, book.name, class.name, class.count," +
                                       "book.count FROM tobuy, book, class WHERE tobuy.bookid" +
                                       "= book.id and tobuy.clsid=class.id");
        ArrayList<Object> arr = new ArrayList<>();
        try {
        	while (rs.next()) {
        		arr.add(rs.getInt(1));		// buy id
        		arr.add(rs.getString(2));	// book name
        		arr.add(rs.getString(3));	// class name
        		arr.add(rs.getInt(4));		// class count
        		arr.add(rs.getInt(5));		// book count
        	}
            m.args = arr.toArray(new Object[0]);
            return;
        } catch (SQLException e) {
        	e.printStackTrace();
        }
    	m.name = "err";
    }
	public void handle_heart(Message m) {
	}
}
