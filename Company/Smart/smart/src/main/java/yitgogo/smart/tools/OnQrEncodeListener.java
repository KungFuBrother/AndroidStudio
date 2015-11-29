package yitgogo.smart.tools;

public class OnQrEncodeListener extends OnMissionListener {

	@Override
	protected void onCache(MissionMessage missionMessage) {
		super.onCache(missionMessage);
		onCache((QrEncodeMissonMessage) missionMessage);
	}

	@Override
	protected void onSuccess(MissionMessage missionMessage) {
		super.onSuccess(missionMessage);
		onSuccess((QrEncodeMissonMessage) missionMessage);
	}

	@Override
	protected void onFail(MissionMessage missionMessage) {
		super.onFail(missionMessage);
		onFail((QrEncodeMissonMessage) missionMessage);
	}

	public void onCache(QrEncodeMissonMessage message) {
	}

	public void onSuccess(QrEncodeMissonMessage message) {
	}

	public void onFail(QrEncodeMissonMessage message) {
	}
}
