package jp.co.yahoo.hackday10.runaway;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RunawayService extends Service {

	private long mStartTime = 0;
	private long mEndTime = 0;
	private int mEnd = END_RUNNING;
	private HashMap<Integer, Hunter> mEncountedHunter;
	private RunawayMegController megController;
	private LocationManager mLocationManager;
	private Location mLocation;
	private RunawayServiceBinder mBinder;
	
	class Hunter {
		public double lat = 0;
		public double lng = 0;
		public int id = 0;
		public int status = 0;
		
		Hunter(JSONObject json) {
			try {
				lat =Double.valueOf(json.getString("lat"));
				lng =Double.valueOf(json.getString("lng"));
				id =Integer.valueOf(json.getString("no"));
			} catch (JSONException e) {
				;
			}
		}
	}

	/**
	 * サーバーと同期する間隔（秒）
	 */
	private final int SYNC_SEC = 2;

	/**
	 * 終了判定
	 */
	public static final int END_RUNNING = 0;
	public static final int END_TIMEUP = 1;
	public static final int END_CHACH = 2;
	public static final int END_AREAOUT = 3;
	public static final int END_GIVEUP = 4;

	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mStartTime = 0;
		mEndTime = 0;
		mBinder = new RunawayServiceBinder();
		mEncountedHunter = new HashMap<Integer, Hunter>();
		megController = new RunawayMegController();
		megController.init();

		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(listner);
        }
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
	public void startGame(long time) {
		Log.d("Service", "GameStart");
		mStartTime = new Date().getTime();
		mEnd = END_RUNNING;
		mEndTime = mStartTime + time * 1000;
		
		if (mLocationManager != null) {
	        mLocationManager.requestLocationUpdates(
	            LocationManager.NETWORK_PROVIDER,
	            0,
	            0,
	            listner);
		}
        
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				int syncCount = 0;
				while (mEnd == END_RUNNING) {
					Log.d("AsyncTask", "doInBackground");
					// カウントダウン
					RunawayService.this.callCountDown();
					syncCount++;
					if (syncCount >= SYNC_SEC) {
						RunawayService.this.callApi();
						syncCount = 0;
					}
					try {
						Thread.sleep(998);
					} catch (Exception e) {
						;
					}
					// 終了判定
					if (mEndTime < (new Date()).getTime()) {
						setEnd(END_TIMEUP);
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
		Log.d("Service", "callApi");
		new Thread(new Runnable() {

			@Override
			public void run() {
				doGet();
				if (isEncounted()) onEncount();
			}
		}).start();
	}
	
	LocationListener listner = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			;
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			;
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			;
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Log.d("LOCATION", "lat/long:"+location.getLatitude()+"/"+location.getLongitude());
			mLocation = location;
		}
	};
	
	synchronized public void doGet( )
	{
	    try
	    {
	    	//位置情報が取れない場合
	    	if (mLocation == null) return;
	    	
	    	StringBuilder url = new StringBuilder();
	    	url.append("http://czk61w6-al5-app000.c4sa.net/api_status.php?lat=").append(mLocation.getLatitude())
	    	.append("&lng=").append(mLocation.getLongitude());
	    	
	    	if (mEncountedHunter.size() > 0) url.append("&hstatus=");
	    	int maxStatus = 0;
	        for (Integer hid : mEncountedHunter.keySet()) {
	        	int status = mEncountedHunter.get(hid).status;
	        	if (maxStatus < status) maxStatus = status;
	        	url.append(hid).append("_").append(status).append(",");
	        }
        	mEncountedHunter.clear();
        	
	    	Log.d("GET", url.toString());
	    	HttpGet method = new HttpGet( url.toString() );

	        DefaultHttpClient client = new DefaultHttpClient();
	        
	        HttpResponse response = client.execute( method );
	        int status = response.getStatusLine().getStatusCode();
	        
	        //ゲームオーバー時
        	if (maxStatus == 2) {
        		setEnd(END_CHACH);
        		return;
        	}
        	
	        if ( status != HttpStatus.SC_OK ) return ;
	        
	        String json = EntityUtils.toString( response.getEntity(), "UTF-8" );
	        Log.d("Service", json);
	        JSONObject jsonObj = new JSONObject(json);
	        int approach = Integer.valueOf(jsonObj.getString("approach"));
	        if (approach != 1) return;
	        
	        //ハンターに見つかった場合は追加
	        JSONArray hunters = jsonObj.getJSONArray("hunters");
	        for (int i=0; i < hunters.length(); i++) {
	        	Hunter h = new Hunter((JSONObject)hunters.get(i));
	        	if (!mEncountedHunter.containsKey(h.id)) {
	        		mEncountedHunter.put(h.id, h);
	        	}
	        }
	    }
	    catch ( Exception e )
	    {
	        e.printStackTrace();
	    }
	}

	/**
	 * エンカウントした時呼ばれる
	 * 
	 * @return 振り切ったかどうか
	 */
	synchronized private int onEncount() {
		Log.d("Service", "onEncount");
		int gameover = 0;
        for (Integer hid : mEncountedHunter.keySet()) {
        	Hunter h = mEncountedHunter.get(hid);
        	if (mLocation == null || (Math.abs(h.lat - mLocation.getLatitude()) <= 0.00005 && Math.abs(h.lng - mLocation.getLongitude()) <= 0.00005)) {
        		Calendar cal = Calendar.getInstance();
        		cal.setTimeInMillis(new Date().getTime());
        		if(cal.get(Calendar.SECOND) % 3 == 0) {
        			gameover = 1;
        		} else {
        			gameover = 2;
        		}
        	}
        	h.status = gameover;
        }
		return 0;
		// くっついたかどうかを渡す
		//return megController.alert(false);
	}

	/**
	 * カウントダウン画面を呼び出す
	 */
	private void callCountDown() {
//		if (mEncountedHunter.size() == 0) {
//			// エンカウント中は呼び出さない
//			megController.normal(getRestTime());
//			return;
//		}
	}

	/**
	 * 終了状態を設定
	 */
	public void setEnd(int end) {
		this.mEnd = end;
		if (end > END_RUNNING) {
			mStartTime = 0;
			mEndTime = 0;
			mLocationManager.removeUpdates(listner);
		}
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
	
	synchronized public boolean isEncounted() {
		return mEncountedHunter.size() > 0;
	}

}
