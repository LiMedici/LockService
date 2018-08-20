package com.example.administrator.lockservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @desc 1像素的Activity
 */
public class SinglePixelActivity extends AppCompatActivity {

    public static final String PIXEL_KEY = "1像素惨案";

    private static final String TAG = "SinglePixelActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pixel);
        initData();
    }

    private void initData() {

        // 获得activity的Window对象，设置其属性
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 1;
        attrParams.width = 1;
        mWindow.setAttributes(attrParams);
        // 绑定SinglePixelActivity到ScreenManager
        ScreenManager.getScreenManagerInstance(this).setSingleActivity(this);
    }

    @Override
    protected void onDestroy() {
        Log.w(TAG,"onDestroy--->1像素保活被终止");
        /** APP被干掉了，我要重启它
        if(!ActivityUtil.isActivityExistsInStack(this)){
            Intent intentAlive = new Intent(this, SplashActivity.class);
            startActivity(intentAlive);
            Log.i(TAG,"SinglePixelActivity---->APP被干掉了，我要重启它");
        }
        */
        super.onDestroy();
    }
}
