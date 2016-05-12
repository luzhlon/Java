package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import Message.Message;

public class Global {
    static Socket sock = null;
    static ObjectOutputStream out;
    static ObjectInputStream in;
    
    static String svIp = "127.0.0.1";
    static int	  svPort = 1234;
    
	public static boolean connect() {
    	try {
            if (sock == null || !sock.isConnected()) {
				sock = new Socket(svIp, svPort);
            	out = new ObjectOutputStream(sock.getOutputStream());
                out.writeObject(new Message("heart", null));
                in = new ObjectInputStream(sock.getInputStream());
                in.readObject();
            }
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return false;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
	}
	public static Message send(Message m) {
        try {
			out.writeObject(m);
            m = (Message) in.readObject();
            return m;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            return null;
		}
	}
	public static Message send(String m, Object...args) {
        return send(new Message(m, args));
	}
    public static void alert(String f, Object... args) {
    	JOptionPane.showMessageDialog(null, String.format(f, args));
    }
}
