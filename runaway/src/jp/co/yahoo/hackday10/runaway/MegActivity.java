package jp.co.yahoo.hackday10.runaway;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.co.olympus.meg40.BluetoothNotEnabledException;
import jp.co.olympus.meg40.BluetoothNotFoundException;
import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegStatus;
import jp.co.yahoo.hackday10.runaway.R.id;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class MegActivity extends Activity implements MegListener {
	private Meg mMeg; // MEGへのコマンド送信を行うインスタンス
	private MegControll mMegCon; // グラフィック描画用
	private NormalThread normalThread = null;
	private RunawayMegController megController;
	private SoundPool sp;
	private int soundIdAlert;
	private int soundIdExp;
	private int status = -1;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEGへの接続要求
	private static final int REQUEST_ENABLE_BT = 2;

	private static final int STATUS_CONNECT = 0;
	private static final int STATUS_NORMAL = 1;
	private static final int STATUS_ALERT = 2;
	private static final int STATUS_LOSE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundIdAlert = sp.load(this, R.raw.alarm, 1);
		soundIdExp = sp.load(this, R.raw.exp, 1);

		// Connect
		Button bConn = (Button) findViewById(id.bConnect);
		bConn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status < STATUS_CONNECT) {
					megConnect();
					status = STATUS_CONNECT;
				}
				try {
					Map<Integer, InputStream> map = new HashMap<Integer, InputStream>();
					AssetManager am = getResources().getAssets();
					map.put(Integer.valueOf(MegControll.MEG_FILE_ID_ALERT1),
							am.open("alert1.png"));
					map.put(Integer.valueOf(MegControll.MEG_FILE_ID_ALERT2),
							am.open("alert2.png"));
					map.put(Integer.valueOf(MegControll.MEG_FILE_ID_LOSE),
							am.open("youlose.png"));
					map.put(Integer.valueOf(MegControll.MEG_FILE_ID_NORMAL),
							am.open("normal.png"));
					mMegCon.init(map);
				} catch (Exception e) {
					Toast.makeText(MegActivity.this, "open asset failed",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Normal
		Button bNormal = (Button) findViewById(id.bNormal);
		bNormal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status != STATUS_CONNECT) {
					return;
				}
				if (normalThread == null) {
					normalThread = new NormalThread(getMegController(), 200000); // milisec
					normalThread.start();
				} else {
					normalThread.displayRestart();
				}
				status = STATUS_NORMAL;
			}
		});
		// Normal
		Button bNormalStop = (Button) findViewById(id.bNormalStop);
		bNormalStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status < STATUS_CONNECT) {
					return;
				}
				if (normalThread != null) {
					normalThread.displayStop();
				}
				status = STATUS_CONNECT;
			}
		});
		// Alert
		Button bAlert = (Button) findViewById(id.bAlert);
		bAlert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status != STATUS_CONNECT) {
					return;
				}
				if (normalThread != null) {
					normalThread.displayStop();
				}
				getMegController().alert(false);
				sp.stop(soundIdExp);
				sp.play(soundIdAlert, 1.0F, 1.0F, 1, 20, 1.0F);
				status = STATUS_ALERT;
			}
		});
		// AlertStop
		Button bAlertStop = (Button) findViewById(id.bAlertStop);
		bAlertStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status < STATUS_CONNECT) {
					return;
				}
				getMegController().stopAlert();
				sp.stop(soundIdAlert);
				status = STATUS_CONNECT;
			}
		});
		// Game over
		Button bGameover = (Button) findViewById(id.bGameover);
		bGameover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status < STATUS_CONNECT) {
					return;
				}
				if (normalThread != null) {
					normalThread.displayStop();
				}
				getMegController().gameOver();
				sp.stop(soundIdAlert);
				sp.play(soundIdExp, 1.0F, 1.0F, 0, 0, 1.0F);
				status = STATUS_CONNECT;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		megDisconnected();
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
				Toast.makeText(this, "Connect to " + address,
						Toast.LENGTH_SHORT).show();

				// addressは"XX:XX:XX:XX:XX:XX"の形式
				mMeg.connect(address);
				// このメソッドからはすぐに返ってくる。接続に成功するとonMegConnectedが、
				// 失敗するとonMegConnectionFailedが呼び出される
			}
			break;

		case REQUEST_ENABLE_BT:
			Toast.makeText(this, getString(R.string.bt_disabled),
					Toast.LENGTH_LONG).show();
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, R.string.bt_enabled, Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
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

	private RunawayMegController getMegController() {
		if (megController == null) {
			megController = new RunawayMegController(mMegCon);
			megController.init();
		}
		return megController;
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
		Toast.makeText(this, ret == 1 ? "delete OK" : "delete NG",
				Toast.LENGTH_SHORT).show();
	}

}
