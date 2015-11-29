package yitgogo.smart.tools;

public class NetworkMissionMessage extends MissionMessage {

	String result = "";

	public NetworkMissionMessage(int progressStatus, String message,
			String result) {
		super(progressStatus, message);
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "RequestMessage [progressStatus=" + progressStatus
				+ ", message=" + message + ", result=" + result + "]";
	}

}
