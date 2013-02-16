package jp.co.yahoo.hackday10.runaway;

import java.util.Date;
import java.util.HashSet;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class RunawayService extends Service {

	private long mStartTime = 0;
	private long mEndTime = 0;
	private int mEnd = END_RUNNING;
	private HashSet<Integer> mEncountedHunter;
	private RunawayMegController megController;
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
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mStartTime = 0;
		mEndTime = 0;
		mEncountedHunter = new HashSet<Integer>();
		megController = new RunawayMegController();
		megController.init();
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
	 * 
	 * @param time
	 */
	public void startGame(int time) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				int syncCount = 0;
				while (mEnd == END_RUNNING) {
					// カウントダウン
					RunawayService.this.callCountDown();
					syncCount++;
					if (syncCount >= SYNC_SEC) {
						RunawayService.this.callApi();
						syncCount = 0;
					}
					try {
						Thread.sleep(990);
					} catch (Exception e) {
						;
					}
					// 終了判定
					if (mEndTime < (new Date()).getTime()) {
						mEnd = END_TIMEUP;
					}
				}
				return null;
			}
		};
		task.execute();
	}

	/**
	 * API呼び出して色々
	 */
	private void callApi() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 0) {
					//
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				onEncount();
				// 返却する
				handler.sendEmptyMessage(1);

			}
		}).start();
	}

	/**
	 * エンカウントした時呼ばれる
	 * 
	 * @return 振り切ったかどうか
	 */
	private int onEncount() {
		// くっついたかどうかを渡す
		return megController.alert(false);
	}

	/**
	 * カウントダウン画面を呼び出す
	 */
	private void callCountDown() {
		if (mEncountedHunter.size() == 0) {
			// エンカウント中は呼び出さない
			megController.normal(getRestTime());
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				// カウントしてね

			}
		}).start();
	}

	/**
	 * 終了状態を設定
	 */
	public void setEnd(int end) {
		this.mEnd = end;
	}

	/**
	 * 終了状態を取得
	 * 
	 * @return
	 */
	public int getEnd() {
		return this.mEnd;
	}

	/**
	 * 残り秒数を取得
	 * 
	 * @return
	 */
	public int getRestTime() {
		return (int) ((mEndTime - (new Date()).getTime()) / 1000);
	}

	public boolean isStarted() {
		return mStartTime != 0;
	}

}
