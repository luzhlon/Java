package server;

import java.io.*;
import java.net.*;

import Global.G;
import Message.*;

public class ThreadConnect extends Thread {
    Socket sock;
    MessageHandler handler;
    ObjectInputStream in;
	ObjectOutputStream out;
    
    public ThreadConnect(Socket sock, MessageHandler handler) {
        this.sock = sock;
        this.handler = handler;
        InetAddress addr = sock.getInetAddress();
        G.output("New connect:%s %d\n", addr.getHostAddress(), sock.getPort());
    }

    public void run() {
        try {
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());
            while(sock.isConnected()) {
                Message msg = (Message) in.readObject();
                handler.handle(msg);
                out.writeObject(msg);
                out.flush();
            }
        } catch (Exception e) {
            return;
        }
    }
}
