import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Created by John on 2015/6/17.
 */
//同步消息处理
public class MessageHandler {
    String mStrLastErr;
    void SendMsg(LinkedList<Object> args) {
        if(args.size()==0) { return; }
        String strMsg = (String) args.get(0);
        String strMethod = "handle_" + strMsg;
        String err = null;
        try {
            Method[] ms = MessageHandler.this.getClass()
                    .getDeclaredMethods();
            Method method = null;
            for(Method m:ms) {
                //Log.d("Method", "'" +m.getName() + "'");
                if(strMethod.equals(m.getName())) {
                    method = m;
                    break;
                }
            } // */
            //Method method = MessageHandler.this.getClass().getDeclaredMethod(strMethod);
            if(method==null) {
                err = "NoSuchMethod";
            } else {
                method.invoke(this, args);
            }
        } catch (Exception e) {
            err = e.toString();
        } finally {
            if(err!=null) {
                System.out.println("MessageHandler ERROR:" + mStrLastErr);
                args.clear();
                return;
            }
            mStrLastErr = err;
        }
    }
    String getLastErr() {
        return mStrLastErr;
    }
}

