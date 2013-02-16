package jp.co.yahoo.hackday10.runaway;

public class AlertThread extends Thread {
	private boolean alertFlg = true;
	private MegControll mMegCon;

	public AlertThread(MegControll mc) {
		mMegCon = mc;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		alertFlg = true;
		try {
			// アラート状態の間、出し続ける
			while (alertFlg) {
				mMegCon.hunterAlert();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * アラート停止
	 */
	public void alertStop() {
		alertFlg = false;
	}
}
