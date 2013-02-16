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

	public void normalMode(boolean begin) {
		imageDraw(10000, 0, 0, begin);
	}

	public void hunterAlert() throws Exception {
		imageDraw(2000, 0, 0, true);
		Thread.sleep(200);
		imageDraw(2001, 0, 0, true);
		Thread.sleep(200);
	}

	public void countDownText(long millisUntilFinished) {
		mMegGraphics.begin();
		clearScreen();
		normalMode(false);
		setFont(35, 0xffffffff);
		textDraw(130, 135, (millisUntilFinished / 1000) + "秒");
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

	public void imageDraw(int id, int x, int y, boolean begin) {
		if (begin) {
			mMegGraphics.begin();
		}
		mMegGraphics.drawImage(id, x, y, new Rect(0, 0, 320, 270));
		if (begin) {
			mMegGraphics.end();
		}
	}
}
