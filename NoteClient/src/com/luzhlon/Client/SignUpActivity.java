package com.luzhlon.Client;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


/**
 * Created by John on 2015/6/17.
 */
public class SignUpActivity extends Activity {
    String username;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signup);
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
    }
    public void onSignUp(View v) {
        String name = ((EditText)findViewById(R.id.editUser)).getText().toString();
        String ps1 = ((EditText)findViewById(R.id.editPasswd)).getText().toString();
        String ps2 = ((EditText)findViewById(R.id.editPasswd2)).getText().toString();
        if(!ps1.equals(ps2)) {
            Global.ToastMessage(this, "两次输入的密码不一致");
            return;
        }
        try {
            Global.CheckUsernamePassword(name, ps1);
        } catch (Exception e) {
            Global.ToastMessage(this, e.toString());
            return;
        }
        username = name;
        Global.ToastMessage("正在注册...");
        //send request to server
        Global.handler.send(new MyMsgHandler.callBack() {
            @Override
            public void onResults(Object[] result) {
                switch ((String)result[0]) {
                    case "yes":
                        Global.ToastMessage("注册成功!");
                        Global.user = SignUpActivity.this.username;
                        Global.NextActivity(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.finish();
                        break;
                    case "no":
                        Global.ToastMessage("注册失败!" + result[1]);
                        break;
                    default:
                        Global.ToastMessage("注册失败!" + "未知原因");
                        break;
                }
            }
        }, "signup", name, ps1);
    }
}