package com.relay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by John on 2015/6/30.
 */
public class RelayClient extends Socket {
    DataOutputStream out;
    DataInputStream  in;
    public RelayClient(String relayerAddr, int port) throws IOException {
        super(relayerAddr, port);
        DataOutputStream out = new DataOutputStream(getOutputStream());
        out.write(RelayServer.getBytes("client"));//表明自己是client
        out.flush();
    }
    public void connect(String server, String con) throws IOException {
        out = new DataOutputStream(getOutputStream());
        out.write(RelayServer.getBytes(server));//要连接的服务器
        out.write(RelayServer.getBytes(con));//新建立的连接的名称
        out.flush();
        in = new DataInputStream(getInputStream());
        //等待中继服务器的回应
        byte[] buf = new byte[32];
        int len = in.read(buf);
        String msg = new String(buf, 0, len);
        switch (msg) {
            case "ok":
                //接下来开始正常数据传输
                System.out.println("Client:Prepare translate data");
                return;
            case "no":
                throw new IOException("Server is unavailable");
        }
    }
    public DataInputStream getInputStream() {
        return in;
    }
    public DataOutputStream getOutputStream() {
        return out;
    }
}
