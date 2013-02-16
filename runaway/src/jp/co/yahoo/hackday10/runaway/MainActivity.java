package jp.co.yahoo.hackday10.runaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	final static private String TAG = MainActivity.class.getName();
	
	private Button mButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate start");
		setContentView(R.layout.main);
		
		mButton = (Button) findViewById(R.id.buttonStart);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(MainActivity.this, TimerActivity.class);
				startActivity(startIntent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume start");
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
