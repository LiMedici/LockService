package com.example.administrator.lockservice;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * @desc:Service 互相监听是否被杀死,互相重启进程
 * @author：李宗好
 * @time: 2017/7/29 0029 16:13
 */
public class DefendManager {

    private static DefendManager mDefendManager;

    private static ArrayMap<String,Class<? extends Service>> mClassMap;

    /**
     * 私有化构造函数
     */
    private DefendManager(){
        mClassMap = new ArrayMap<>();
    }

    /**
     * 单例设计模式
     * @return
     */
    public static DefendManager getInstance(){
        if(null == mDefendManager){
            synchronized (DefendManager.class){
                if(null == mDefendManager){
                    mDefendManager = new DefendManager();
                }
            }
        }
        return mDefendManager;
    }

    /**
     * 判断某所有服务是否正常工作
     * @param context
     * @return
     */
    @CheckResult
    public static boolean isAllServiceWorked(final Context context) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        int worked= 0;
        for (Map.Entry<String,Class<? extends Service>> entry:mClassMap.entrySet()) {
            for (int i = 0; i < runningService.size(); i++) {
                Class<? extends Service> serviceClass = entry.getValue();
                String runningServiceName = runningService.get(i).service.getClassName().toString();
                if (runningServiceName.equals(serviceClass.getCanonicalName())) {
                    Log.v(DefendManager.class.getCanonicalName(),runningServiceName+" ======= 正在运行");
                    worked ++;
                }
            }
        }
        Log.v(DefendManager.class.getCanonicalName(),"正在运行进程数 ======= " + worked);
        return worked == mClassMap.size();

    }

    /**
     * 启动某个服务
     * @param context 上下文
     * @param clx 启动的Service clx对象
     */
    public void startService(final Context context, final Class<? extends Service> clx){
        // 启动Service
        Intent intent = new Intent(context, clx);
        // 高版本Service启动方式
        String canonicalName = clx.getCanonicalName();
        intent.setPackage(canonicalName);
        context.startService(intent);
        // remove clx , 取消重复
        mClassMap.remove(canonicalName);
        mClassMap.put(canonicalName,clx);
    }

    /**
     * 启动所有可能被killed的service
     */
    public void startAllKilledService(final Context context){
        for (Map.Entry<String,Class<? extends Service>> entry:mClassMap.entrySet()) {
            Class<? extends Service> serviceClass = entry.getValue();
            startService(context,serviceClass);
        }
    }


}
