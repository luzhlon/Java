package Message;

import java.io.Serializable;

public class Message implements Serializable {
	public String name;
	public Object[] args;
	public Message(String n, Object... a) {
		name = n;
		args = a;
	}
}
