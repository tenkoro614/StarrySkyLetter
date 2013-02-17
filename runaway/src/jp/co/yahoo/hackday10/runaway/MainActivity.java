package jp.co.yahoo.hackday10.runaway;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.co.olympus.meg40.BluetoothNotEnabledException;
import jp.co.olympus.meg40.BluetoothNotFoundException;
import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegStatus;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	final static private String TAG = MainActivity.class.getName();
	private Meg mMeg; // MEG
	private MegControll mMegCon; // グラフィック描画用

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEGへの接続要求

	private ImageView mButton;
	private ImageView mSplash;
	private SeekBar mAreaSeek;
	private SeekBar mHunterSeek;
	private SeekBar mTimeSeek;
	private TextView mAreaText;
	private TextView mHunterText;
	private TextView mTimeText;
	private ImageView mWhite;
	private long mTimes = 0;
	final private static int[] AREA_SELECTS = {100, 300, 500, 1000, 5000};
	final private static int[] HUNTER_SELECTS = {1, 5, 10, 20, 100};
	final private static String[] TIME_SELECTS = {"30分", "1時間", "3時間", "1日", "無制限"};
	final private static long[] TIME_SELECTS_LONG = {30*60, 60*60, 180*60, 24*60*60, 0};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate start");
		setContentView(R.layout.main);
		
		mWhite = (ImageView) findViewById(R.id.imageWhite);
		mButton = (ImageView) findViewById(R.id.buttonStart);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlphaAnimation animation = new AlphaAnimation(1, 0);
				animation.setDuration(500);
				animation.setAnimationListener(new Animation.AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						mWhite.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						;
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {

						Intent startIntent = new Intent(MainActivity.this, TimerActivity.class);
						startIntent.putExtra("TIMES", mTimes);
						startActivity(startIntent);
						mWhite.setVisibility(View.GONE);
					}
				});
				mWhite.startAnimation(animation);
			}
		});
		mSplash = (ImageView) findViewById(R.id.imageSplash);
		mAreaText = (TextView) findViewById(R.id.textArea);
		mAreaSeek =(SeekBar) findViewById(R.id.seekArea);
		mAreaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mAreaText.setText(AREA_SELECTS[progress] + "M");
			}
		});
		mHunterText = (TextView) findViewById(R.id.textHunter);
		mHunterSeek =(SeekBar) findViewById(R.id.seekHunter);
		mHunterSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mHunterText.setText(HUNTER_SELECTS[progress] + "人");
			}
		});
		
		mTimeText = (TextView) findViewById(R.id.textTime);
		mTimeSeek =(SeekBar) findViewById(R.id.seekTime);
		mTimeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTimeText.setText(TIME_SELECTS[progress]);
				mTimes = TIME_SELECTS_LONG[progress];
			}
		});
		
		mAreaSeek.setProgress(2);
		mHunterSeek.setProgress(2);
		mTimeSeek.setProgress(2);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					mGoneSplash.sendEmptyMessage(0);
				} catch(Exception e) {
					;
				}
			}
		}).start();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume start");

		//サービススタート
		Intent serviceIntent = new Intent(this, RunawayService.class);
		startService(serviceIntent);
		//bind
		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
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
			
			if (binder.getService().isStarted()) {
				Intent startIntent = new Intent(MainActivity.this, TimerActivity.class);
				startActivity(startIntent);
			}
		}
	};
	
	/**
	 * スプラッシュ画像をフェードアウト
	 */
	Handler mGoneSplash = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AlphaAnimation animation = new AlphaAnimation(1, 0);
			animation.setDuration(1500);
			animation.setAnimationListener(new Animation.AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					;
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					;
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					mSplash.setVisibility(View.GONE);
				}
			});
			mSplash.startAnimation(animation);
		}
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "ストップ");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent serviceIntent = new Intent(this, RunawayService.class);
		//bind
		unbindService(mConnection);
		stopService(serviceIntent);
		finish();
		return super.onMenuItemSelected(featureId, item);
	}
}
