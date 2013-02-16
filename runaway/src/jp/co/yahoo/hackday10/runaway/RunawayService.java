package jp.co.yahoo.hackday10.runaway;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class RunawayService extends Service {

	private long mStartTime = 0;
	private long mEndTime = 0;
	private int mEnd = END_RUNNING;
	/**
	 * サーバーと同期する間隔（秒）
	 */
	private final int SYNC_SEC = 3;
	
	/**
	 * 終了判定
	 */
	public static final int END_RUNNING = 0;
	public static final int END_TIMEUP = 1;
	public static final int END_CHACH = 2;
	public static final int END_AREAOUT = 3;
	
	
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
				int syncCount = 0;
				while (mEnd == END_RUNNING) {
					//カウントダウン
					RunawayService.this.callCountDown();
					syncCount ++;
					if (syncCount >= SYNC_SEC) {
						RunawayService.this.callApi();
						syncCount = 0;
					}
					try {
						Thread.sleep(990);
					} catch(Exception e) {
						;
					}
					//終了判定
					if (mEndTime < (new Date()).getTime()) {
						mEnd = END_TIMEUP;
					}
				}
				return null;
			}
		};
		task.execute();
	}
	
	private void callApi() {
		
	}
	
	private void onEncount() {
		
	}
	
	/**
	 * カウントダウン画面を呼び出す
	 */
	private void callCountDown() {
		;
	}
	
	/**
	 * 終了状態を設定
	 */
	public void setEnd(int end) {
		this.mEnd = end;
	}
	
	/**
	 * 終了状態を取得
	 * @return
	 */
	public int getEnd() {
		return this.mEnd;
	}
	
	/**
	 * 残り秒数を取得
	 * @return
	 */
	public int getRestTime() {
		return (int)((mEndTime - (new Date()).getTime()) /1000);
	}
	
	public boolean isStarted() {
		return mStartTime != 0;
	}

}
