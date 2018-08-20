package com.example.administrator.lockservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SecondActivity extends AppCompatActivity
    implements WorkService.Callback{

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> listData;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mListView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listData);
        mListView.setAdapter(mAdapter);
        // 启动请求结果回调
        WorkService.setCallback(this);
    }

    @Override
    public void onResult(String result) {
        // 运行在子线程
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String data = "网络请求成功:  "+new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date())+":===========>" + (listData.size() + 1);
                Log.e("WorkService","WorkService: ====== " + data);
                listData.add(data);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 启动任务
     * @param view
     */
    public void start(View view){
        Intent intent = new Intent(WorkService.WORK_RECEIVER_ACTION);
        intent.putExtra(WorkService.EXTRA_WORK_FLAG,true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 暂停任务
     * @param view
     */
    public void stop(View view){
        Intent intent = new Intent(WorkService.WORK_RECEIVER_ACTION);
        intent.putExtra(WorkService.EXTRA_WORK_FLAG,false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void show(Context context) {
        Intent intent = new Intent(context,SecondActivity.class);
        context.startActivity(intent);
    }
}
