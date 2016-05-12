package Message;

import java.lang.reflect.Method;

public class MessageHandler {
	String err = "";
	boolean error(String s) {
		err = s;
		return false;
	}
	public String error() {
		return err;
	}
    public boolean handle(String msg, Object... args) {
    	return handle(new Message(msg, args));
    }
    
    public boolean handle(Message msg) {
        String method = "handle_" + msg.name;
        try {
            Method ms = MessageHandler.this
				            		  .getClass()
				            		  .getMethod(method, Message.class);
                    
            if(ms == null) {
            	return error("No that method");
            } else {
                ms.invoke(this, msg);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	return error(e.toString());
        }
        return true;
    }
}
