package jp.co.yahoo.hackday10.runaway;

import android.os.CountDownTimer;

public class NormalThread extends CountDownTimer {
	private MegControll mMegCon;
	private boolean displayFlg = true;

	public NormalThread(MegControll mc, int startTime) {
		super(startTime, 1000);
		mMegCon = mc;
	}

	public void onTick(long millisUntilFinished) {
		if (displayFlg) {
			mMegCon.normalMode(millisUntilFinished);
		}
	}

	public void onFinish() {
	}

	public void displayStop() {
		displayFlg = false;
	}
}
