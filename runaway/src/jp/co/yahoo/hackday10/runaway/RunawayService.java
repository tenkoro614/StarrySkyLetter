package jp.co.yahoo.hackday10.runaway;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class RunawayService extends Service {

	private int mStartTime = 0;
	private int mEndTime = 0;
	private boolean mEnd = false;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mStartTime = 0;
		mEndTime = 0;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public class RunawayServiceBinder extends Binder {
		public RunawayService getService() {
			return RunawayService.this;
		}
	}
	
	/**
	 * 通信したり色々
	 * @param time
	 */
	public void startGame(int time) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				while (!mEnd) {
					
				}
				
				
				return null;
			}
		};
		task.execute();
	}
	
	public int getTime() {
		return mStartTime;
	}
	
	public boolean isStarted() {
		return mStartTime != 0;
	}

}
