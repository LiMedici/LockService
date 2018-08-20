package com.example.administrator.lockservice;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc 广播服务, 继续在后台运行, 应对从服务器推送通知事件。
 * @time:2016年12月18日 下午1:17:25
 */
public class WorkService extends HostService {

    private static final int SERVICE_ID = 1 << 1;

    private static final String TAG = WorkService.class.getCanonicalName();

    public static final String WORK_RECEIVER_ACTION = WorkFlagReceiver.class.getCanonicalName();

    public static final String EXTRA_WORK_FLAG = "EXTRA_WORK_FLAG";

    private WorkFlagReceiver mFlagReceiver;

    private Timer mTimer;

    private static Callback mCallback;

    private volatile boolean mWorkFlag;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果服务被killed了,会重新启动该服务,并传入之前的intent 除非进程被关闭了。
        flags = Service.START_REDELIVER_INTENT;

        if (Build.VERSION.SDK_INT < 18) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(SERVICE_ID, new Notification());
        } else {
            //进行灰色保活手段
            Intent innerIntent = new Intent(this, WorkInnerService.class);
            startService(innerIntent);
            startForeground(SERVICE_ID, new Notification());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart()...");
        registerBroadcastReceiver();
    }

    /**
     * 启动任务
     */
    private void doWork(){
        // 启动Timer
        if(mTimer == null){
            mTimer = new Timer();
        }

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://www.baidu.com");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    String result = response.toString();

                    if(!TextUtils.isEmpty(result) && mCallback != null){
                        mCallback.onResult(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        },0,1000*2);// 2S一个请求
    }

    /**
     * 关闭任务
     */
    private void closeWork(){
        // 关闭Timer
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 测试Demo 静态方法处理
     * @param callback
     */
    public static void setCallback(Callback callback){
        mCallback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()...");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()...");
        unRegisterBroadcastReceiver();
    }


    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class WorkInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }

    /**
     * 启动注册
     */
    private void registerBroadcastReceiver(){
        mFlagReceiver = new WorkFlagReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WORK_RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mFlagReceiver,filter);
    }

    /**
     * 取消注册
     */
    private void unRegisterBroadcastReceiver(){
        if(mFlagReceiver != null){
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mFlagReceiver);
        }
    }

    /**
     * @desc 更新任务Flag
     * @author 李宗好
     */
    public class WorkFlagReceiver extends BroadcastReceiver {

        public WorkFlagReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(WORK_RECEIVER_ACTION.equals(action)){
                boolean flag = intent.getBooleanExtra(EXTRA_WORK_FLAG,false);
                mWorkFlag = flag;
                if(mWorkFlag){
                    // doWork
                    doWork();
                }else{
                    closeWork();
                }
            }

        }
    }

    public interface Callback{
        void onResult(String result);
    }



}
