package com.example.administrator.lockservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @Desc 守护线程。保证核心服务不被杀死  服务互拉
 * @author 李宗好
 * @time:203月20日上午10:09:25
 */
public class DefendService extends HostService {

	private static final int SERVICE_ID = 1 << 0;

	private MediaPlayer mMediaPlayer;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_NOT_STICKY;

		if (Build.VERSION.SDK_INT < 18) {
			// API < 18 ，此方法能有效隐藏Notification上的图标
			startForeground(SERVICE_ID, new Notification());
		} else {
			//进行灰色保活手段
			Intent innerIntent = new Intent(this, DefendInnerService.class);
			startService(innerIntent);
			startForeground(SERVICE_ID, new Notification());
		}

		//注册广播
		IntentFilter filter = new IntentFilter(RestartServiceReceiver.RESTART_SERVICE_ACTION);
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new RestartServiceReceiver(),filter);

		//播放无声音乐
		ThreadManager.getLongPool().execute(new Runnable() {
			@Override
			public void run() {
				startPlayMusic();
			}
		});

		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
	}

	/**
	 * 播放无声音乐
	 */
	private void startPlayMusic(){
		try{
			mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
		}catch (Exception e){
			e.printStackTrace();
			Log.e("DefendService",e.toString());
		}

	}

	private void stopPlayMusic(){
		if(mMediaPlayer != null){
			mMediaPlayer.stop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopPlayMusic();

		//发送广播,重新启动服务
		Intent mIntent = new Intent();
		mIntent.setAction(RestartServiceReceiver.RESTART_SERVICE_ACTION);
		mIntent.putExtra("clazz",this.getClass());
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(mIntent);
		Log.e("DefendService",this.getClass().getCanonicalName() + ":" + "被杀死了");
	}


	/**
	 * 给 API >= 18 的平台上用的灰色保活手段
	 */
	public static class DefendInnerService extends Service {

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
	
}
