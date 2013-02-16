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

	public void init(Map<Integer, InputStream> map) throws Exception {
		clearScreen();
		mMegGraphics.begin();
		for (Map.Entry<Integer, InputStream> e : map.entrySet()) {
			removeImage(e.getKey().intValue());
			registerImage(e.getKey().intValue(), e.getValue());
		}
		mMegGraphics.end();
	}

	public void normalMode(long millisUntilFinished) {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(10000, 0, 0);
		setFont(35, 0xffffffff);
		textDraw(130, 135, (millisUntilFinished / 1000) + "秒");
		mMegGraphics.end();
	}

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

	public void gameClear() {
		mMegGraphics.begin();
		clearScreen();
		imageDraw(10000, 0, 0);
		mMegGraphics.end();
	}

	public void clearScreen() {
		mMegGraphics.clearScreen();
	}

	public void setFont(int size, int color) {
		mMegGraphics.setFontSize(size);
		mMegGraphics.setFontColor(color);
	}

	public void textDraw(int x, int y, String text) {
		mMegGraphics.drawString(x, y, text);
	}

	public void registerImage(int id, InputStream is) throws Exception {
		Bitmap bm = BitmapFactory.decodeStream(is);
		mMegGraphics.registerImage(id, bm);
	}

	public void removeImage(int id) {
		mMegGraphics.removeImage(id);
	}

	public void imageDraw(int id, int x, int y) {
		mMegGraphics.drawImage(id, x, y, new Rect(0, 0, 320, 270));
	}
}
