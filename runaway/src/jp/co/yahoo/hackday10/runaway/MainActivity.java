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
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class MainActivity extends Activity implements MegListener {
	final static private String TAG = MainActivity.class.getName();
	private Meg mMeg; // MEG
	private MegControll mMegCon; // グラフィック描画用

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEGへの接続要求

	private Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate start");
		setContentView(R.layout.main);

		megConnect();

		mButton = (Button) findViewById(R.id.buttonStart);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent startIntent = new Intent(MainActivity.this,
						TimerActivity.class);
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
		megDisconnected();
		Log.d(TAG, "onDestroy start");
	}

	private void megConnect() {
		// Bluetooth接続できるかどうかチェックする
		// 接続できなければ、アプリを終了
		if (mMeg == null) {
			try {
				// MEGはシングルトンパターン
				// 最初のgetInstance呼び出しではインスタンス生成時に例外が投げられることがある
				mMeg = Meg.getInstance();
				// MEGのイベント監視のハンドラを登録
				mMeg.registerMegListener(this);

				// MEGのグラフィックス機能を使うクラスの生成
				mMegCon = new MegControll(mMeg);
			} catch (BluetoothNotFoundException e) {
				Toast.makeText(this, "Bluetoothアダプターが見つかりません",
						Toast.LENGTH_LONG).show();
				finish();
				return;
			} catch (BluetoothNotEnabledException e) {
				Toast.makeText(this, "Bluetoothが無効になっています¥n有効にしてください",
						Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		}
		// mMegは非null
		if (mMeg.isConnected()) {
			// 接続済み
		} else // 未接続
		{
			// Bluetooth接続できるペアリング済みデバイスのリストを表示するアクティビティ（ダイアログ）を開始する。
			// アクティビティが終了したら、onActivityResult()
			// に終了コードとして、REQUEST_CONNECT_DEVICEを返す。
			// MEGへの接続はonActivityResult()で実行される。
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		}
	}

	private void megDisconnected() {
		// mMegは非null、かつ、接続済み
		if (mMeg != null && mMeg.isConnected()) {
			mMeg.disconnect();
		}
	}

	// 他のアクティビティから結果を受信したときのコールバック
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		// Bluetooth接続できるペアリング済みデバイスのリストを表示するアクティビティ（ダイアログ）が終了した場合
		// ユーザーが指定したBluetoothの接続アドレスの取得に成功したら、そのアドレスのMEGに接続を開始する
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// addressは"XX:XX:XX:XX:XX:XX"の形式
				mMeg.connect(address);
				Toast.makeText(this, "Connect to " + address,
						Toast.LENGTH_SHORT).show();

				try {
					Map<Integer, InputStream> map = new HashMap<Integer, InputStream>();
					AssetManager am = getResources().getAssets();
					map.put(Integer.valueOf(2000), am.open("alert1.png"));
					map.put(Integer.valueOf(2001), am.open("alert2.png"));
					map.put(Integer.valueOf(2003), am.open("youlose.png"));
					map.put(Integer.valueOf(10000), am.open("normal.png"));
					mMegCon.init(map);
				} catch (Exception e) {
					Toast.makeText(this, "open asset failed",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	// 以下、MEGのコールバック(MegListenerのメソッド)
	/** Bluetooth接続完了時のコールバック */
	@Override
	public void onMegConnected() {
		String deviceName = mMeg.getDeviceName();
		Toast.makeText(this, "Connected to " + deviceName + ".",
				Toast.LENGTH_SHORT).show();
	}

	/** Bluetooth接続失敗時のコールバック */
	@Override
	public void onMegConnectionFailed() {
		Toast.makeText(this, "Meg connection failed.", Toast.LENGTH_LONG)
				.show();
	}

	/** Bluetooth切断時のコールバック */
	@Override
	public void onMegDisconnected() {
		Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
	}

	/** スリープ開始受信時のコールバック */
	@Override
	public void onMegSleep() {
		Toast.makeText(this, "スリープ状態に入ります", Toast.LENGTH_LONG).show();
	}

	/** ステータス受信時のコールバック */
	@Override
	public void onMegStatusChanged(MegStatus status) {
		String LCD = new String(status.getLcdStatus() ? "LCD ON¥n"
				: "LCD OFF¥n");
		String LorR = new String(status.getLRmode() ? "左装着¥n" : "右装着¥n");
		String isVoltageLow = new String(status.getVoltageStatus() ? "電圧低下¥n"
				: "電圧正常¥n");
		String AutoSleepValue = new String("自動スリープ:")
				+ String.valueOf(status.getAutoSleepValue())
				+ new String("秒¥n");
		String BrightnessValue = new String("LCD輝度:")
				+ String.valueOf(status.getBrightnessValue());
		Toast.makeText(
				this,
				"Meg Status¥n" + LCD + LorR + isVoltageLow + AutoSleepValue
						+ BrightnessValue, Toast.LENGTH_SHORT).show();
	}

	/** 電源電圧低下受信時のコールバック */
	@Override
	public void onMegVoltageLow() {
		Toast.makeText(this, "電圧が低下しています", Toast.LENGTH_LONG).show();
	}

	/** アプリボタン押下受信時のコールバック */
	@Override
	public void onMegKeyPush(int push, int release) {
		if (push != 0) {
			Toast.makeText(this, "アプリボタンが押されました", Toast.LENGTH_SHORT).show();
		} else if (release != 0) {
			Toast.makeText(this, "アプリボタンが離されました", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 方位・仰角データ受信時のコールバック
	 * 
	 * @param dir
	 *            方位データ (0-3599)
	 * @param angle
	 *            仰角データ (0-255)
	 * */
	@Override
	public void onMegDirectionChanged(int dir, int angle) {

	}

	/** 加速度データ受信時のコールバック */
	@Override
	public void onMegAccelChanged(int x, int y, int z) {

	}

	/** GraphicsCommand開始受信時のコールバック */
	@Override
	public void onMegGraphicsCommandStart(int ret) {

	}

	/** GraphicsCommand終了受信時のコールバック */
	@Override
	public void onMegGraphicsCommandEnd(int ret) {

	}

	/** コンテキスト設定受信時のコールバック */
	@Override
	public void onMegSetContext(int ret) {
	}

	/** Image削除受信時のコールバック */
	@Override
	public void onMegDeleteImage(int ret) {
	}
}
