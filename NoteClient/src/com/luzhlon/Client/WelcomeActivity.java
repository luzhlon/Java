package com.luzhlon.Client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.zip.GZIPOutputStream;

/**
 * Created by John on 2015/6/24.
 */
public class WelcomeActivity extends Activity {
    ImageView imgWorld;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        Global.Initialize(getBaseContext());//全局初始化
        gotoMainActivity();
        /*
         * 初始化图标ImageView
        ImageView hello = (ImageView) findViewById(R.id.imageHello);
        imgWorld = (ImageView) findViewById(R.id.imageWorld);
        /*
         * 定义逐渐透明的动画(AlphaAnimation)
         * 构造方法的两个参数是float(最大是1.0f,最小是0.0f)
         * 第一个是开始的时候透明度:
         * 第二个是结束的时候透明度
         * 设置动画时间
        AlphaAnimation aniHello = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation aniWorld = new AlphaAnimation(0.0f, 1.0f);
        aniHello.setDuration(1500);//
        aniHello.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                aniWorld.setDuration(1500);//
                imgWorld.setAnimation(aniWorld);//第一个动画结束后播放第二个
            }
        });
        hello.setAnimation(aniHello);
        aniWorld.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                gotoMainActivity();
            }
        });
         */
    }
    public void gotoMainActivity() {
        //Goto MainActivity
        Intent intent = new Intent();
        intent.setClass(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        //Remove welcome from the activity stack
        WelcomeActivity.this.finish();
    }
    @Override
    public void onResume() {
        super.onResume();
        Global.CurAct = this;
    }
}