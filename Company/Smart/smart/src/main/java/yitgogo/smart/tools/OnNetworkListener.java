package yitgogo.smart.tools;

public class OnNetworkListener extends OnMissionListener {

	@Override
	protected void onCache(MissionMessage missionMessage) {
		super.onCache(missionMessage);
		onCache((NetworkMissionMessage) missionMessage);
	}

	@Override
	protected void onSuccess(MissionMessage missionMessage) {
		super.onSuccess(missionMessage);
		onSuccess((NetworkMissionMessage) missionMessage);
	}

	@Override
	protected void onFail(MissionMessage missionMessage) {
		super.onFail(missionMessage);
		onFail((NetworkMissionMessage) missionMessage);
	}

	public void onCache(NetworkMissionMessage message) {
	}

	public void onSuccess(NetworkMissionMessage message) {
	}

	public void onFail(NetworkMissionMessage message) {
	}
}
