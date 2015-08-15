package com.luzhlon.Client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by John on 2015/6/25.
 */
public class SuggestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.suggestion);
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
    }
    public void onSubmit(View v) {
        EditText e = (EditText)findViewById(R.id.editSuggest);
        if(e.getText().toString().isEmpty()) {
            Global.ToastMessage(this, "亲，请输入您的建议哦~~");
            return;
        }
        e.setText("");
        Global.ToastMessage(this, "正在提交意见......");
        Global.ToastMessage(this, "谢谢您的支持！");
    }
}