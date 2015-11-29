package yitgogo.smart.task;

import yitgogo.smart.tools.API;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;
import android.content.Context;

public class BianminTask {

	public static void getPhoneChargeInfo(Context context, String phoneno,
			int pervalue, float mctype, OnNetworkListener onNetworkListener) {
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_PHONE_CHARGE_INFO);
		networkContent.addParameters("phoneno", phoneno);
		networkContent.addParameters("pervalue", pervalue + "");
		if (mctype > 0) {
			if (mctype > 1) {
				networkContent.addParameters("mctype", (int) mctype + "");
			} else {
				networkContent.addParameters("mctype", mctype + "");
			}
		}
		MissionController.startNetworkMission(context, networkContent,
				onNetworkListener);
	}

	public static void phoneCharge(Context context, String memberAccount,
			String phoneno, int pervalue, float mctype,
			OnNetworkListener onNetworkListener) {
		NetworkContent networkContent = new NetworkContent(
				API.API_BIANMIN_PHONE_CHARGE);
		networkContent.addParameters("memberAccount", memberAccount);
		networkContent.addParameters("phoneno", phoneno);
		networkContent.addParameters("pervalue", pervalue + "");
		if (mctype > 0) {
			if (mctype > 1) {
				networkContent.addParameters("mctype", (int) mctype + "");
			} else {
				networkContent.addParameters("mctype", mctype + "");
			}
		}
		MissionController.startNetworkMission(context, networkContent,
				onNetworkListener);
	}
}
