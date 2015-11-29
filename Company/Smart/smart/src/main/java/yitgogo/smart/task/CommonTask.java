package yitgogo.smart.task;

import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;
import android.content.Context;

public class CommonTask {

	public static void checkUpdate(Context context,
			OnNetworkListener onNetworkListener) {
		NetworkContent networkContent = new NetworkContent(API.API_UPDATE);
		MissionController.startNetworkMission(context, networkContent,
				onNetworkListener);
	}

	public static void getMachineState(Context context,
			OnNetworkListener onNetworkListener) {
		NetworkContent networkContent = new NetworkContent(
				API.API_MACHINE_STATE);
		networkContent.addParameters("machine", Device.getDeviceCode());
		MissionController.startNetworkMission(context, networkContent,
				onNetworkListener);
	}

}
