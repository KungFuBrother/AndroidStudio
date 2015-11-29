package yitgogo.smart.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class OnMissionListener {

	public static final int PROGRESS_START = 0;
	public static final int PROGRESS_CACHE = 1;
	public static final int PROGRESS_FAILED = 2;
	public static final int PROGRESS_SUCCESS = 3;
	public static final int PROGRESS_FINISH = 4;

	Handler handler = new Handler(Looper.myLooper()) {
		public void handleMessage(android.os.Message msg) {
			operateMessage((MissionMessage) msg.obj);
		};
	};

	private void operateMessage(MissionMessage missionMessage) {
		switch (missionMessage.getProgressStatus()) {

		case PROGRESS_START:
			onStart();
			break;

		case PROGRESS_CACHE:
			onCache(missionMessage);
			break;

		case PROGRESS_FAILED:
			onFail(missionMessage);
			break;

		case PROGRESS_SUCCESS:
			onSuccess(missionMessage);
			break;

		case PROGRESS_FINISH:
			onFinish();
			break;
		default:
			break;
		}
	}

	public void sendMessage(MissionMessage missionMessage) {
		handler.sendMessage(Message.obtain(handler, 0, missionMessage));
	}

	public void onStart() {
		LogUtil.logInfo("Request Status", "onStart");
	}

	protected void onCache(MissionMessage missionMessage) {
		LogUtil.logInfo("Request Status",
				"onCache----------" + missionMessage.toString());
	}

	protected void onFail(MissionMessage missionMessage) {
		LogUtil.logInfo("Request Status",
				"onFail----------" + missionMessage.toString());
	}

	protected void onSuccess(MissionMessage missionMessage) {
		LogUtil.logInfo("Request Status", "onSuccess----------"
				+ missionMessage.toString());
	}

	public void onFinish() {
		LogUtil.logInfo("Request Status", "onFinish");
	}

}