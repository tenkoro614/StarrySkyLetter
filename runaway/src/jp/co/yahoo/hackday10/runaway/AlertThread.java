package jp.co.yahoo.hackday10.runaway;

public class AlertThread extends Thread {
	private boolean alertFlg = true;
	private MegControll mMegCon;

	public AlertThread(MegControll mc) {
		mMegCon = mc;
	}

	@Override
	public void run() {
		alertFlg = true;
		try {
			while (alertFlg) {
				mMegCon.hunterAlert();
			}
		} catch (Exception e) {
		}
	}

	public void alertStop() {
		alertFlg = false;
	}
}
