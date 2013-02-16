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
			// removeImage(e.getKey().intValue());
			registerImage(e.getKey().intValue(), e.getValue());
		}
		normalMode(0);
		mMegGraphics.end();
	}

	/**
	 * 通常モード
	 */
	public void normalMode(long restTime) {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(10000, 0, 0);
		setFont(35, 0xffffffff);
		textDraw(130, 135, restTime + "秒");
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
		imageDraw(2000, 0, 0);
		mMegGraphics.end();
		Thread.sleep(200);
		mMegGraphics.begin();
		imageDraw(2001, 0, 0);
		mMegGraphics.end();
		Thread.sleep(200);
	}

	/**
	 * ゲームクリア
	 */
	public void gameClear() {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(10000, 0, 0);
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
