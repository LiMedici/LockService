package com.example.administrator.lockservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * ***************************************
 * @desc: 对1像素界面的管理
 * @author：李宗好
 * @time: 2017/11/20 0020 09:46
 * @email：lzh@cnbisoft.com
 * @version：
 * @history:
 * ***************************************
 */
public class ScreenManager {

    private static final String TAG = "ScreenManager";

    private static ScreenManager mScreenManager;

    private Context mContext;
    /**
     * 使用弱引用，防止内存泄漏
     */
    private WeakReference<Activity> mActivityRef;

    private ScreenManager(Context mContext){
        this.mContext = mContext;
    }

    /**
     * 单例模式
     * @param context
     * @return
     */
    public static ScreenManager getScreenManagerInstance(Context context){
        if(mScreenManager == null){
            mScreenManager = new ScreenManager(context);
        }
        return mScreenManager;
    }

    /**
     * 获得SinglePixelActivity的引用
     * @param mActivity Activity
     */
    public void setSingleActivity(Activity mActivity){
        mActivityRef = new WeakReference<>(mActivity);
    }

    /**
     * 启动SinglePixelActivity
     */
    public void startActivity(){
        Log.w(TAG,"准备启动SinglePixelActivity...");
        Intent intent = new Intent(mContext,SinglePixelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 结束SinglePixelActivity
     */
    public void finishActivity(){
        Log.w(TAG,"准备结束SinglePixelActivity...");
        if(mActivityRef != null){
            Activity mActivity = mActivityRef.get();
            if(mActivity != null){
                mActivity.finish();
            }
        }
    }
}
