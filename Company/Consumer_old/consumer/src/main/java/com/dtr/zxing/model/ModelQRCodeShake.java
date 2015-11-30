package com.dtr.zxing.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "data": { "activityCode": "活动码", "activityName": "活动名称" },
 *       "codeType": 3 }
 */
public class ModelQRCodeShake {

	String activityCode = "", activityName = "";

	public ModelQRCodeShake(JSONObject object) {
		if (object != null) {
			if (object.has("activityCode")) {
				if (!object.optString("activityCode").equalsIgnoreCase("null")) {
					activityCode = object.optString("activityCode");
				}
			}
			if (object.has("activityName")) {
				if (!object.optString("activityName").equalsIgnoreCase("null")) {
					activityName = object.optString("activityName");
				}
			}
		}
	}

	public String getActivityCode() {
		return activityCode;
	}

	public String getActivityName() {
		return activityName;
	}

}
