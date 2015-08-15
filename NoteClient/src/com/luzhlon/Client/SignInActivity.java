package com.luzhlon.Client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by John on 2015/6/17.
 */
public class SignInActivity extends Activity {
    EditText editUser;
    EditText editPasswd;
    SharedPreferences sp;
    String username;
    String password;

    void Init() {
        editUser = (EditText)findViewById(R.id.editUser);
        editPasswd = (EditText)findViewById(R.id.editPasswd);
        String user = Global.sp.getString(Global.USERNAME, "");
        String passwd = Global.sp.getString(Global.PASSWORD, "");
        if(!user.isEmpty())
            editUser.setText(user);
        if(!passwd.isEmpty())
            editPasswd.setText(passwd);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signin);
        Init();
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
    }

    //登录按钮
    public void onSignIn(View v) {
        String name = ((EditText)findViewById(R.id.editUser)).getText().toString();
        String passwd = ((EditText)findViewById(R.id.editPasswd)).getText().toString();
        //check the username
        try {
            Global.CheckUsernamePassword(name, passwd);
        } catch (Exception e) {
            Global.ToastMessage(this, e.toString());
            return;
        }
        Global.handler.send(new MyMsgHandler.callBack() {
            public void onResults(Object[] result) {
                switch ((String) result[0]) {
                    case "yes":
                        Global.ToastMessage("登录成功!");
                        Global.NextActivity(SignInActivity.this, MainActivity.class);
                        Global.user = name;
                        SignInActivity.this.finish();
                        break;
                    case "no":
                        Global.ToastMessage("登录失败!" + result[1]);
                        break;
                    default:
                        Global.ToastMessage("登录失败!" + "未知原因");
                        break;
                }
            }
        }, "signin", name, passwd);
        //保存用户名密码
        SharedPreferences.Editor edit = Global.sp.edit();
        edit.putString(Global.USERNAME, name);
        edit.putString(Global.PASSWORD, passwd);
        edit.commit();
    }
    //注册
    public void onSignUp(View v) {
        Global.NextActivity(this, SignUpActivity.class);
    }
}