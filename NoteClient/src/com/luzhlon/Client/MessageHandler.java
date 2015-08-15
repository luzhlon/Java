package com.luzhlon.Client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by John on 2015/6/13.
 * By CodeSoul
 */
/**
 * 你只需要继承这个类并添加一些 handle_XXX 的方法就
 * 可以通过SendMsg("XXX", ...)来异步调用这些方法
 * handle_XXX方法接收一个参数数组 Object... args
 */
//异步处理消息类
 public class MessageHandler extends HandlerThread {
    private MyHandler mHandler;

    MessageHandler() {
        super("MessageHandler");
        start();
        mHandler = new MyHandler(this);
    }
    //发送消息并交由相应的方法处理
    void SendMsg(String strMsg, Object... args) {
        mHandler.mStrMsg = strMsg;
        Message msg = Message.obtain();
        msg.obj = args;
        mHandler.sendMessage(msg);
    }
    //获取最后一个错误，可能为null
    String getLastErr() {
        return mHandler.mStrLastErr;
    }
    //内部类MyHandler把分发给各方法
    class MyHandler extends Handler {
        String  mStrMsg;
        String  mStrLastErr;
        MessageHandler msgHandler;
        MyHandler(MessageHandler handler) {
            super(handler.getLooper());
            msgHandler = handler;
        }
        @Override
        public void handleMessage(Message msg) {
            String strMethod = "handle_" + mStrMsg;
            String err = null;
            try {
            //* 动态调用方法
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
            //不知道为什么这个方法获取不到
            //Method method = MessageHandler.this.getClass().getDeclaredMethod(strMethod);
            if(method==null) {
                err = "NoSuchMethod";
            } else {
                method.invoke(msgHandler, msg.obj);
            }
        } catch (Exception e) {
            err = e.toString();
        } finally {
            if(err!=null) Log.e("MessageHandler ERROR:", mHandler.mStrLastErr);
            mStrLastErr = err;
        }
        }
    }
}

