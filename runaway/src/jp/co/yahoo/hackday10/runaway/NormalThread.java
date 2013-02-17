package jp.co.yahoo.hackday10.runaway;

import android.os.CountDownTimer;

public class NormalThread extends CountDownTimer {
	private RunawayMegController megController;
	private boolean displayFlg = true;

	public NormalThread(RunawayMegController mc, int startTime) {
		super(startTime, 1000);
		megController = mc;
	}

	public void onTick(long millisUntilFinished) {
		if (megController.getAlertStatus() == RunawayMegController.ALAERT_STATUS_ESCAPE) {
			if (displayFlg) {
				megController.normal((int) (millisUntilFinished / 1000));
			}
		}
	}

	public void onFinish() {
		megController.gameClear();
	}

	public void displayRestart() {
		displayFlg = true;
	}

	public void displayStop() {
		displayFlg = false;
	}
}
