package com.luzhlon.Client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by John on 2015/6/25.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
    }
}