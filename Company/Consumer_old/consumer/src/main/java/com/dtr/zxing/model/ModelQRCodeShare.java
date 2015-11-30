package com.dtr.zxing.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "userCode": "邀请码", "userAccount": "用户账号" }
 */
public class ModelQRCodeShare {

	String userCode = "", userAccount = "";

	public ModelQRCodeShare(JSONObject object) {
		if (object != null) {
			if (object.has("userCode")) {
				if (!object.optString("userCode").equalsIgnoreCase("null")) {
					userCode = object.optString("userCode");
				}
			}
			if (object.has("userAccount")) {
				if (!object.optString("userAccount").equalsIgnoreCase("null")) {
					userAccount = object.optString("userAccount");
				}
			}
		}
	}

	public String getUserCode() {
		return userCode;
	}

	public String getUserAccount() {
		return userAccount;
	}

}
