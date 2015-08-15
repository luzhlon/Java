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
        out.write(RelayServer.getBytes("client"));//�����Լ���client
        out.flush();
    }
    public void connect(String server, String con) throws IOException {
        out = new DataOutputStream(getOutputStream());
        out.write(RelayServer.getBytes(server));//Ҫ���ӵķ�����
        out.write(RelayServer.getBytes(con));//�½��������ӵ�����
        out.flush();
        in = new DataInputStream(getInputStream());
        //�ȴ��м̷������Ļ�Ӧ
        byte[] buf = new byte[32];
        int len = in.read(buf);
        String msg = new String(buf, 0, len);
        switch (msg) {
            case "ok":
                //��������ʼ�������ݴ���
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
