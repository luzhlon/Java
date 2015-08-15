import java.io.*;
import java.net.*;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by John on 2015/6/12.
 */
class Message implements Serializable {
    public String msg;
    public String data;

    Message(String msg, String data) {
        this.msg = msg;
        this.data = data;
    }
}

/**
 * Created by John on 2015/6/7.
 */
//处理每一个连接的线程类
public class ThreadConnect extends Thread {
    Socket sock;
    MessageHandler handler;
    ObjectInputStream in;
    ObjectOutputStream out;
    ThreadConnect(Socket sock, MessageHandler handler) {
        this.sock = sock;
        this.handler = handler;
        InetAddress addr = sock.getInetAddress();
        System.out.printf("New connect:%s %d\n", addr.getHostAddress(), sock.getPort());
    }

    public void run() {
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            in = new ObjectInputStream(sock.getInputStream());
            while(sock.isConnected()) {
                LinkedList<Object> msg = (LinkedList<Object>) in.readObject();
                handler.SendMsg(msg);//交由MessageHandler处理
                out.writeObject(msg);//将处理结果返回给客户端
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("user logout.");
        }
    }
}
