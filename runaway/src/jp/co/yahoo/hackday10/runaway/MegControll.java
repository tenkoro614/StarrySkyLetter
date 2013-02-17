package jp.co.yahoo.hackday10.runaway;

import java.io.InputStream;
import java.util.Map;

import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegGraphics;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class MegControll {
	private MegGraphics mMegGraphics; // グラフィック描画用
	public static int MEG_FILE_ID_ALERT1 = 3000;
	public static int MEG_FILE_ID_ALERT2 = 3001;
	public static int MEG_FILE_ID_LOSE = 3002;
	public static int MEG_FILE_ID_NORMAL = 3003;

	public MegControll(Meg mMeg) {
		// MEGのグラフィックス機能を使うクラスの生成
		mMegGraphics = new MegGraphics(mMeg);
	}

	/**
	 * MEG初期化
	 */
	public void init(Map<Integer, InputStream> map) throws Exception {
		mMegGraphics.begin();
		for (Map.Entry<Integer, InputStream> e : map.entrySet()) {
			removeImage(e.getKey().intValue());
			registerImage(e.getKey().intValue(), e.getValue());
		}
		mMegGraphics.end();
//		normalMode(0);
	}

	/**
	 * 通常モード
	 */
	public void normalMode(long restTime) {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(MEG_FILE_ID_NORMAL, 0, 0);
		setFont(35, 0xffffffff);
//		setFont(35, 0xff000000);
		// 時間計算
		int min = (int) (restTime / 60);
		int sec = (int) (restTime % 60);
		if (min <= 0) {
			textDraw(140, 135, sec + "秒");
		} else {
			textDraw(130, 135, min + "分 " + sec + "秒");
		}
		mMegGraphics.end();
	}

	/**
	 * ハンターアラート
	 * 
	 * @throws Exception
	 *             Exception
	 */
	public void hunterAlert() throws Exception {
		mMegGraphics.begin();
		imageDraw(MEG_FILE_ID_ALERT1, 0, 0);
		mMegGraphics.end();
		Thread.sleep(200);
		mMegGraphics.begin();
		imageDraw(MEG_FILE_ID_ALERT2, 0, 0);
		mMegGraphics.end();
		Thread.sleep(200);
	}

	/**
	 * ゲームクリア
	 */
	public void gameClear() {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(MEG_FILE_ID_NORMAL, 0, 0);
		mMegGraphics.end();
	}

	/**
	 * ゲームオーバー
	 */
	public void gameOver() {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(MEG_FILE_ID_LOSE, 0, 0);
		mMegGraphics.end();
	}

	private void clearScreen() {
		mMegGraphics.clearScreen();
	}

	private void setFont(int size, int color) {
		mMegGraphics.setFontSize(size);
		mMegGraphics.setFontColor(color);
	}

	private void textDraw(int x, int y, String text) {
		mMegGraphics.drawString(x, y, text);
	}

	private void registerImage(int id, InputStream is) throws Exception {
		Bitmap bm = BitmapFactory.decodeStream(is);
		mMegGraphics.registerImage(id, bm);
	}

	private void removeImage(int id) {
		mMegGraphics.removeImage(id);
	}

	private void imageDraw(int id, int x, int y) {
		mMegGraphics.drawImage(id, x, y, new Rect(0, 0, 320, 270));
	}
}
