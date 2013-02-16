package jp.co.yahoo.hackday10.runaway;

import java.io.InputStream;
import java.util.List;

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

	public void init(List<InputStream> l) throws Exception {
		mMegGraphics.begin();
		clearScreen();
		int id = 2000;
		for (InputStream is : l) {
			removeImage(id);
			registerImage(id, is);
			id++;
		}
		mMegGraphics.end();
	}

	public void hunterAlert() throws Exception {
		imageDraw(2000, 0, 0);
		Thread.sleep(200);
		imageDraw(2001, 0, 0);
		Thread.sleep(200);
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
		mMegGraphics.begin();
		mMegGraphics.drawImage(id, x, y, new Rect(0, 0, 320, 270));
		mMegGraphics.end();
	}
}
