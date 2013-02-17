package jp.co.yahoo.hackday10.runaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	final static private String TAG = MainActivity.class.getName();
	
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
	}
	
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy start");
	}
	
}
