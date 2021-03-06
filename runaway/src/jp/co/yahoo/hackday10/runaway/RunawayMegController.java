package jp.co.yahoo.hackday10.runaway;

import java.io.InputStream;
import java.util.Map;

import jp.co.olympus.meg40.BluetoothNotEnabledException;
import jp.co.olympus.meg40.BluetoothNotFoundException;
import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegStatus;
import android.media.AudioManager;
import android.media.SoundPool;

public class RunawayMegController implements MegListener {
	private Meg mMeg; // MEGへのコマンド送信を行うインスタンス
	private MegControll mMegCon; // グラフィック描画用
	private AlertThread alertThread = null;

	public static final int ALAERT_STATUS_ESCAPE = 0;
	public static final int ALAERT_STATUS_SHAKE = 1;
	public static final int ALAERT_STATUS_LOSE = 2;
	public static final int ESCAPE_CNT = 10;

	private int _alertStatus = ALAERT_STATUS_ESCAPE;
	private int _old_x = 0;
	private int _shake = 0;

	public RunawayMegController() {
	}

	public RunawayMegController(MegControll megcon) {
		mMegCon = megcon;
	}

	public boolean init() {
		// Bluetooth接続できるかどうかチェックする
		if (mMeg == null) {
			try {
				// MEGはシングルトンパターン
				// 最初のgetInstance呼び出しではインスタンス生成時に例外が投げられることがある
				mMeg = Meg.getInstance();
				// MEGのイベント監視のハンドラを登録
				// mMeg.registerMegListener(this);

				// MEGの操作クラス
				if (mMegCon == null) {
					mMegCon = new MegControll(mMeg);
				}
			} catch (BluetoothNotFoundException e) {
				return false;
			} catch (BluetoothNotEnabledException e) {
				return false;
			}
		}
		return true;
	}

	public void connect(String address, Map<Integer, InputStream> map)
			throws Exception {
		// addressは"XX:XX:XX:XX:XX:XX"の形式
		mMeg.connect(address);
		// 初期処理
		mMegCon.init(map);
	}

	public void disconnected() {
		// mMegは非null、かつ、接続済み
		if (mMeg != null && mMeg.isConnected()) {
			mMeg.disconnect();
		}
	}

	public boolean isConnected() {
		if (mMeg != null && mMeg.isConnected()) {
			return true;
		}
		return false;
	}

	public void normal(int restTime) {
		if (isConnected()) {
			mMegCon.normalMode(restTime);
		}
	}

	public void gameClear() {
		if (isConnected()) {
			mMegCon.gameClear();
		}
	}

	public void gameOver() {
		if (isConnected()) {
			stopAlert();
			mMegCon.gameOver();
		}
	}

	public int alert(boolean catchFlg) {
		if (isConnected()) {
			if (alertThread == null) {
				alertThread = new AlertThread(mMegCon);
				alertThread.start();
				// sensor開始
				// mMeg.startAccelerometer(Meg.SENSOR_MIDDLE);
				_alertStatus = ALAERT_STATUS_SHAKE;
				_shake = 0;
			}
			if (catchFlg && _alertStatus == ALAERT_STATUS_SHAKE) {
				stopAlert();
				mMegCon.gameOver();
				return ALAERT_STATUS_LOSE;
			}
		}
		return getAlertStatus();
	}

	public void stopAlert() {
		if (isConnected()) {
			if (alertThread != null) {
				alertThread.alertStop();
				_alertStatus = ALAERT_STATUS_ESCAPE;
				alertThread = null;
				// mMeg.stopAccelerometer();
				_old_x = 0;
			}
		}
	}

	public int getAlertStatus() {
		return _alertStatus;
	}

	/** 加速度データ受信時のコールバック */
	@Override
	public void onMegAccelChanged(int x, int y, int z) {
		// 逆に移動したか？
		if ((_old_x < 0 && x >= 0) || (_old_x >= 0 && x < 0)) {
			_shake++;
		}
		if (getAlertStatus() == ALAERT_STATUS_SHAKE && _shake >= ESCAPE_CNT) {
			stopAlert();
		}
		_old_x = x;
	}

	@Override
	public void onMegConnected() {
	}

	@Override
	public void onMegConnectionFailed() {
	}

	@Override
	public void onMegDeleteImage(int arg0) {
	}

	@Override
	public void onMegDirectionChanged(int arg0, int arg1) {
	}

	@Override
	public void onMegDisconnected() {
	}

	@Override
	public void onMegGraphicsCommandEnd(int arg0) {
	}

	@Override
	public void onMegGraphicsCommandStart(int arg0) {
	}

	@Override
	public void onMegKeyPush(int arg0, int arg1) {
	}

	@Override
	public void onMegSetContext(int arg0) {
	}

	@Override
	public void onMegSleep() {
	}

	@Override
	public void onMegStatusChanged(MegStatus arg0) {
	}

	@Override
	public void onMegVoltageLow() {
	}

}
