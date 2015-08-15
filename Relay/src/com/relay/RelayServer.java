package com.relay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by John on 2015/6/30.
 */
public class RelayServer extends Socket {
    final static int PRO_SIZE = 32;
    String addr;
    int    port;
    static byte[] getBytes(String msg) {
        byte[] b = new byte[PRO_SIZE];
        for (int i = 0; i < PRO_SIZE; i++) b[i] = 0;
        byte[] f = msg.getBytes();
        int less = f.length < PRO_SIZE ? f.length : PRO_SIZE;
        for (int i = 0; i < less; i++) b[i] = f[i];
        return b;
    }
    public RelayServer(String relayerAddr, int port, String name) throws IOException {
        super(relayerAddr, port);
        this.addr = relayerAddr;
        this.port = port;
        DataOutputStream out = new DataOutputStream(getOutputStream());
        out.write(getBytes("server")); //�����Լ��Ƿ�����
        out.write(getBytes(name)); //������������
        out.flush();
    }
    public Socket accept() throws IOException {
        DataInputStream in = new DataInputStream(getInputStream());
        byte[] buf = new byte[32];
        int len = in.read(buf);
        String con = new String(buf, 0, len);//������ӵ�����
        //������Ӧ(respond)����
        Socket sock = new Socket(addr, port);
        DataOutputStream rout = new DataOutputStream(sock.getOutputStream());
        rout.write(getBytes("respond"));//�����Լ��ǻ�Ӧ�ͻ��˵� ����
        rout.write(getBytes(con));//�����жϷ������Լ���Ӧ�����ĸ�����
        rout.flush();
        DataInputStream rin = new DataInputStream(sock.getInputStream());
        len = rin.read(buf);
        String msg = new String(buf, 0, len);
        System.out.println("ServerMsg:" + msg);
        switch (msg) {
            case "ok": //�����ɹ���׼����������
                System.out.println("Server:Prepare translate data");
                return sock;
            case "no"://����ʧ�ܣ�
                throw new IOException("Make connect failure");
        }
        return sock;
    }
}
