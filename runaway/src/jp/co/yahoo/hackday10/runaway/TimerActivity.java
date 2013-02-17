package jp.co.yahoo.hackday10.runaway;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimerActivity extends Activity {
	final static private String TAG = TimerActivity.class.getName();
	
	private TextView mHour;
	private TextView mSec;
	private long mTimes;
	private LinearLayout mTimerLayout;
	private ImageView mCountDown;
	private ImageView mGiveUpButton;
	private LinearLayout mBg;
	private RunawayService mService;
	private TextView mAlert;
	private MediaPlayer mp_end;
	private MediaPlayer mp;
	
	final private static int[] COUNT_IMAGES = {R.drawable.countdown_1, R.drawable.countdown_2, R.drawable.countdown_3};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate start");
		setContentView(R.layout.timer);
		if (getIntent().getExtras() != null) mTimes = getIntent().getExtras().getLong("TIMES");
		mHour = (TextView)findViewById(R.id.textHour);
		mSec = (TextView)findViewById(R.id.textSec);
		
		mGiveUpButton = (ImageView) findViewById(R.id.buttonGiveUp);
		mTimerLayout = (LinearLayout) findViewById(R.id.layoutTimer);
		mCountDown = (ImageView) findViewById(R.id.imageCountDown);
		mBg = (LinearLayout) findViewById(R.id.layoutBg);
		mAlert = (TextView) findViewById(R.id.alert);
		
		mGiveUpButton.setVisibility(View.GONE);
		mGiveUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mService.setEnd(RunawayService.END_GIVEUP);
				finish();
			}
		});
		mTimerLayout.setVisibility(View.GONE);
		mBg.setVisibility(View.GONE);

		mp = MediaPlayer.create(this, R.raw.alarm);
		mp.setLooping(true);
		mp_end = MediaPlayer.create(this, R.raw.exp);
	}

	/**
	 * サービス接続
	 */
	ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("ServiceConnection", "onServiceDisconnected start");
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("ServiceConnection", "onServiceConnected start");
			RunawayService.RunawayServiceBinder binder = (RunawayService.RunawayServiceBinder) service;
			mService = binder.getService();
			
			if (mService.isStarted()) {
				startTimer();
			} else {
				dispCount(3);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume start");
		Intent serviceIntent = new Intent(this, RunawayService.class);
		//bind
		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * カウントダウン実施
	 * @param cnt
	 */
	private void dispCount(final int cnt) {
		if (cnt == 0) {
			//カウントダウン終了時
			mService.startGame(mTimes);
			startTimer();
			return;
		}
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		ScaleAnimation scaleAnimation = new ScaleAnimation((float)1.5, (float)0.8, (float)1.5, (float)0.8);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setDuration(999);
		animationSet.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				Log.d("ANIME", "onAnimationStart");
				mCountDown.setImageResource(COUNT_IMAGES[cnt-1]);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				;
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Log.d("ANIME", "onAnimationEnd");
				mCountDown.setVisibility(View.GONE);
				dispCount(cnt - 1);
			}
		});
		mCountDown.setVisibility(View.VISIBLE);
		mCountDown.startAnimation(animationSet);
	}
	
	/**
	 * タイマー始動
	 */
	private void startTimer() {
		mBg.setVisibility(View.VISIBLE);
		mTimerLayout.setVisibility(View.VISIBLE);
		mGiveUpButton.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (mService.getRestTime() >= 0) {
					drawHandler.sendEmptyMessage(0);
					try {
						Thread.sleep(500);
					} catch (Exception e) {
						;
					}
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (mService.getEnd() > 0) {
							mAlert.setVisibility(View.VISIBLE);
							mAlert.setText("Game Over");
							mp.pause();
							mp_end.start();
							return;
						}
					}
				});
			}
		}).start();
	}
	
	/**
	 * タイマー描画
	 */
	private Handler drawHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("TimerActivity", "call drawHandler");
			long time = mService.getRestTime();
			mHour.setText(Integer.toString((int)(time / 60)));
			mSec.setText(String.format("%02d", time % 60));
			if (mService.isEncounted()) {
				mAlert.setVisibility(View.VISIBLE);
				mAlert.setText("ハンター接近！！");
				mp.start();
			} else {
				mAlert.setVisibility(View.GONE);
				if (mp.isPlaying()) mp.pause();
			}
		};
	};

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause start");
		unbindService(mConnection);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy start");
		mp.release();
		mp_end.release();
	}
}
