package jp.co.yahoo.hackday10.runaway;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class TimerActivity extends Activity {
	final static private String TAG = TimerActivity.class.getName();
	
	private TextView mTimer;
	private TextView mRest;
	private RunawayService mService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate start");
		setContentView(R.layout.timer);
		
		mTimer = (TextView) findViewById(R.id.textTimer);
		mRest = (TextView) findViewById(R.id.textRest);
	}

	//serviceConnection
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
		}
	};
	
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
