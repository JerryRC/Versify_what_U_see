package com.example.poetrywhatusee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import me.jessyan.autosize.AutoSizeConfig;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //禁止文字大小被系统影响
        AutoSizeConfig.getInstance()
                .setBaseOnWidth(false)
                .setExcludeFontScale(true)
                .setDesignHeightInDp(720);

        super.onCreate(savedInstanceState);
        //加载启动界面
        setContentView(R.layout.activity_launch);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        int time = 1000;  //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this,
                        FullscreenActivity.class));
                LaunchActivity.this.finish();
            }
        }, time);
    }
}
