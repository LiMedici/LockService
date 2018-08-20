package com.example.administrator.lockservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    private ScreenManager mScreenManager;
    private ScreenReceiverUtil mScreenReceiverUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 数据的初始化操作
        DefendManager.getInstance().startService(this,WorkService.class);
        DefendManager.getInstance().startService(this,DefendService.class);
        // 监听屏幕状态,显示1像素页面,使应用锁屏至后台能够存活
        mScreenManager = ScreenManager.getScreenManagerInstance(this);
        mScreenReceiverUtil = new ScreenReceiverUtil(this);
        mScreenReceiverUtil.begin(mScreenListener);
    }

    private ScreenReceiverUtil.ScreenStateListener mScreenListener = new ScreenReceiverUtil.ScreenStateListener() {
        @Override
        public void onScreenOn() {
            // 移出"1像素屏幕"
            mScreenManager.finishActivity();
            // 数据的初始化操作
            DefendManager.getInstance().startService(SplashActivity.this,WorkService.class);
        }

        @Override
        public void onScreenOff() {
            // 接到锁屏广播，将SportsActivity切换到可见模式
            // "咕咚"、"乐动力"、"悦动圈"就是这么做滴
            // 那么，我们就制造个"1像素"惨案
            mScreenManager.startActivity();
        }

        @Override
        public void onUserPresent() {

        }
    };

    public void enter(View view){
        SecondActivity.show(this);
        finish();
    }
}
