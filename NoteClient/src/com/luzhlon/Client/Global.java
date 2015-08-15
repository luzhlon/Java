package com.luzhlon.Client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * Created by John on 2015/6/24.
 */
public class Global {
    static final String CONFIG = "config";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String SERVER_ADDR = "server_addr";
    static final String SERVER_PORT = "server_port";
    static final String LAST_SELECT = "last_select";
    static final String TEXTITEM = "textItem";
    static final String TEXTITEML = "textItemL";

    static String server_ip;
    static int server_port;
    static SharedPreferences sp;

    static MainActivity MainAct;
    static Activity     CurAct;
    static MyMsgHandler handler;
    static String user = "";//只有用户在线时才设置为非空

    static void LoadConfig(Context con) {
        sp = con.getSharedPreferences(Global.CONFIG, con.MODE_PRIVATE);
        Global.server_ip = sp.getString(Global.SERVER_ADDR, "192.168.191.1");
        Global.server_port = sp.getInt(Global.SERVER_PORT, 4320);
    }
    static void Initialize(Context con) {
        LoadConfig(con);//加载配置
        Global.handler = new MyMsgHandler();
    }

    static void NextActivity(Context con, Class cls) {
        Intent intent = new Intent();
        intent.setClass(con, cls);
        con.startActivity(intent);
    }
    static void ToastMessage(Context con, String msg) {
        Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
    }
    static void ToastMessage(String msg) {
        Toast.makeText(MainAct, msg, Toast.LENGTH_SHORT).show();
    }
    static class UsernamePasswordException extends Exception {
        String error;
        UsernamePasswordException(String err) {
            error = err;
        }
        public String toString() {
            return error;
        }
    }
    static void CheckUsernamePassword(String u, String p)
            throws UsernamePasswordException {
        if(u==null || u.isEmpty())
            throw new UsernamePasswordException("用户名不能为空");
        if(p==null || p.isEmpty())
            throw new UsernamePasswordException("密码不能为空");
        if(u.length()>=16)
            throw new UsernamePasswordException("用户名太长(16字符以内)");
        if(p.length()>=32)
            throw new UsernamePasswordException("密码太长(32字符以内)");
    }
    static String GetDateTime() {
        SimpleDateFormat format =
                new SimpleDateFormat("HH:mm MM/dd/yyyy");
        String time = format.format(new Date(System.currentTimeMillis()));
        return time;
    }
}
