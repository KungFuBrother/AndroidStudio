package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelGetApdateAddress {

	String firstId = "", secondId = "", thirdId = "";
	ModelAddress address = new ModelAddress();

	public ModelGetApdateAddress() {
		super();
	}

	public ModelGetApdateAddress(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("firstId")) {
				if (!object.getString("firstId").equalsIgnoreCase("null")) {
					firstId = object.optString("firstId");
				}
			}
			if (object.has("secondId")) {
				if (!object.getString("secondId").equalsIgnoreCase("null")) {
					secondId = object.optString("secondId");
				}
			}
			if (object.has("thirdId")) {
				if (!object.getString("thirdId").equalsIgnoreCase("null")) {
					thirdId = object.optString("thirdId");
				}

			}
			JSONObject addressObject = object
					.optJSONObject("updateMemberAddress");
			if (addressObject != null) {
				address = new ModelAddress(addressObject);
			}
		}
	}

	public String getFirstId() {
		return firstId;
	}

	public String getSecondId() {
		return secondId;
	}

	public String getThirdId() {
		return thirdId;
	}

	public ModelAddress getAddress() {
		return address;
	}

}
