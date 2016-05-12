package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        ServerSocket ser;
        try {
            ser = new ServerSocket(1234, 5);
            while(true) {
                Socket sock = ser.accept();
                new ThreadConnect(sock,
                    		new MyHandler()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
