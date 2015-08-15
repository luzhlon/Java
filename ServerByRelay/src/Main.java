import com.relay.RelayClient;
import com.relay.RelayServer;

import java.io.*;
import java.net.Socket;

class MyThread extends Thread {
    Socket sock;
    MyThread(Socket sock) {
        this.sock = sock;
    }
    public void run() {
        try {
            //ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());
            byte[] buf = new byte[10];
            while (true) {
                int len = in.read(buf);
                System.out.print(new String(buf, 0, len));
            }
            //ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            //OutputStream out = sock.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Main {

    public static void main(String[] args) {
        try {
            RelayServer server = new RelayServer("127.0.0.1", 4320, "cloud");
            while(true) {
                Socket sock = server.accept();
                new MyThread(sock).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
