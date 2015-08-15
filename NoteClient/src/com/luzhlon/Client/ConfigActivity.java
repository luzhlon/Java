package com.luzhlon.Client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by John on 2015/6/25.
 */
public class ConfigActivity extends Activity {
    EditText editAddr;
    EditText editPort;
    void Init() {
        editAddr = (EditText) findViewById(R.id.editSerAddr);
        editPort = (EditText) findViewById(R.id.editSerPort);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        Init();
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
        //读取数据
        String serAddr = Global.sp.getString(Global.SERVER_ADDR, "");
        int serPort = Global.sp.getInt(Global.SERVER_PORT, 4320);
        editAddr.setText(serAddr);
        editPort.setText("" + serPort);
    }
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()!=KeyEvent.ACTION_DOWN) return false;
        if(keyCode!=KeyEvent.KEYCODE_BACK) return false;
        new AlertDialog.Builder(this).setTitle("是否要保存？")
                .setPositiveButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        ConfigActivity.this.finish();
                    }
                })
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //保存数据
                        SharedPreferences.Editor edit = Global.sp.edit();
                        String serAddr = editAddr.getText().toString();
                        String strPort = editPort.getText().toString();
                        int serPort = Integer.parseInt(strPort);
                        edit.putString(Global.SERVER_ADDR, serAddr);
                        edit.putInt(Global.SERVER_PORT, serPort);
                        edit.commit();
                        dialogInterface.cancel();
                        ConfigActivity.this.finish();
                    }
                }).show();
        return true;
    }
    public void onLogOut(View v) {
        Global.user = "";
        //Global.handler.SendMsg("logout");
        finish();
    }
    public void onAbout(View v) {
        Global.NextActivity(this, AboutActivity.class);
    }
    public void onSuggest(View v) {
        Global.NextActivity(this, SuggestActivity.class);
    }
}