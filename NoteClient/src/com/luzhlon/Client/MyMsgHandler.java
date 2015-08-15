package com.luzhlon.Client;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

class MyMsgHandler extends MessageHandler {
    static final String DEBUG = "DEBUG";
    static final String EXCEPTION = "Exception";
    Socket sock;
    ObjectInputStream in;
    ObjectOutputStream out;
    HashMap<String, Object> results = new HashMap<String, Object>();

    public static int internal = 200; //millisecond
    public static int overtime = 5000; //millisecond

    Object[] GetResult(String msg) {
        LinkedList<Object> result = (LinkedList<Object>)results.get(msg);
        if (result == null) return null;
        Object[] objs = new Object[result.size()];
        int i = 0;
        for (Object obj : result) {
            objs[i] = obj; i++;
        }
        results.put(msg, null);//清除这个结果
        return objs;
    }

    static class callBack implements Runnable {
        Object[] result;
        callBack() {
        }
        void setResult(Object[] result) {
            this.result = result;
        }
        void showAll(Object[] result) {
            for(Object obj:result) {
                Log.d(DEBUG, obj.toString());
            }
        }
        public void onResults(Object[] result) {
        }
        public void onOverTime() {
            Global.ToastMessage(Global.CurAct, "服务器返回超时");
            //Global.handler.sock = null;
        }
        @Override
        public void run() {
            if(result==null) onOverTime();
            else onResults(result);
        }
    }
    //当服务器返回时
    void WhenResults(callBack cb, String msg) {
        //以UI线程方式运行
        new Thread(){
            public void run() {
                try {
                    int count = overtime / internal;
                    if(cb==null) return;
                    for (int i = 0; i < count; i++) {
                        Object[] result = GetResult(msg);
                        if (result != null) { //得到结果
                            cb.setResult(result);
                            break;
                        }
                        sleep(internal);//每隔internal微秒检查一下返回结果
                    }
                    //在UI线程中回调
                    Global.MainAct.runOnUiThread(cb);
                } catch (Exception e) {
                    Log.e(EXCEPTION, e.toString());
                }
            }
        }.start();
    }

    //向服务器发送数据并设置一个回调
    public void send(callBack cb, Object... args) {
        SendMsg("send", args);
        WhenResults(cb, (String)args[0]);
    }

    void handle_connect(Object... args) {
        try {
            sock = new Socket(Global.server_ip, Global.server_port);
        } catch (Exception e) {
            Log.i(EXCEPTION, e.toString());
            //sock = null;
        }
    }
    static ReentrantLock lock = new ReentrantLock();
    void handle_send(Object... args) {
        if (sock == null) {
            Global.ToastMessage("连接已断开。");
            //Log.d(DEBUG, "Socket is not connectd.");
            Global.ToastMessage(Global.CurAct, "正在连接服务器...");
            handle_connect();
            if(sock==null) {
                Global.ToastMessage(Global.CurAct, "服务器连接失败");
                return;
            }
        }
        lock.lock();//锁定下面的代码
        LinkedList<Object> request = new LinkedList<>();
        for(Object obj:args) request.addLast(obj);//转化成LinkedList
        try {
            if(out==null) {
                out = new ObjectOutputStream(sock.getOutputStream());
            }
            if(in==null) {
                in = new ObjectInputStream(sock.getInputStream());
            }
            out.writeObject(request);
            out.flush();
            LinkedList<Object> result = (LinkedList<Object>)in.readObject();
            String msg = (String)result.get(0);
            result.remove(0); //去掉首元素，即msg
            results.put(msg, result); //将服务器的响应结果存于HashMap中
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        } finally {
            lock.unlock();//解锁
        }
    }

    void handle_set_selection(Object... args) {
        try {
            ListView list = (ListView)args[0];
            int i = (int)args[1];
            list.setSelection(i);
        } catch (Exception e) {
            Log.e(EXCEPTION, e.toString());
        }
    }

}

