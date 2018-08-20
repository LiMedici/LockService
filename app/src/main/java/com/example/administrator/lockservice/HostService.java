package com.example.administrator.lockservice;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @desc:唤醒电源锁的公共类  和服务互相守护的基类
 * @time: 2017/7/29 0029 17:58
 */
public class HostService extends Service {

    private DefendManager mDefendManager;

    private Timer mTimer = null;

    /**
     * 唤醒锁
     */
    private PowerManager.WakeLock mWakeLock = null;


    @Override
    public void onCreate() {
        super.onCreate();
        mDefendManager = DefendManager.getInstance();
        mTimer = new Timer();
        mTimer.schedule(new DefendTimerTask(), 0, 1000);
        acquireWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        START_STICKY:如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。
//                     随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。
//                     如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。

//        START_NOT_STICKY："非粘性的"。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。

//        START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。

//        START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
        return super.onStartCommand(intent, flags, startId);
    }

    private void acquireWakeLock()
    {
        if (null == mWakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "HostService");
            if (null != mWakeLock)
            {
                Log.e(this.getClass().getCanonicalName()," ======= 唤醒电源锁");
                mWakeLock.acquire();
            }
        }
    }
    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != mWakeLock)
        {
            Log.e(this.getClass().getCanonicalName()," ======= 释放电源锁");
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    /**
     * @Desc 待执行的任务
     * @author 李宗好
     * @time:2017年3月20日上午10:17:37
     */
    private class DefendTimerTask extends TimerTask {

        @Override
        public void run() {
            boolean serviceWorked = mDefendManager.isAllServiceWorked(HostService.this);
            Log.e(this.getClass().getCanonicalName(),"HostService ----------> " + serviceWorked);
            if(!serviceWorked) {
                mDefendManager.startAllKilledService(HostService.this);
            }
        }

    }


}
