import com.relay.RelayClient;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            RelayClient client = new RelayClient("127.0.0.1", 4320);
            client.connect("cloud", "cloud1");
            DataOutputStream out = client.getOutputStream();
            Scanner in = new Scanner(System.in);
            out.write("aaaaaaaaa".getBytes());
            out.flush();
            out.write("bbbbbbbbb".getBytes());
            out.flush();
            out.write("ccccccccc".getBytes());
            out.flush();
            out.write("ddddddddd".getBytes());
            out.flush();
            out.write("eeeeeeeee".getBytes());
            out.flush();
            out.write("fffffffff".getBytes());
            out.flush();
            out.write("ggggggggg".getBytes());
            out.flush();
            while(true) {
                String line = in.nextLine();
                out.write(line.getBytes());
                out.flush();
            }
            /*
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            String str = (String)in.readObject();
            System.out.println(str);
            in.close();
            // */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
