package yitgogo.consumer.product.model;

import org.json.JSONObject;

public class ModelBrand {

	String id = "", brandName = "", brevityCode = "";

	public ModelBrand(JSONObject object) {
		if (object.has("id")) {
			if (!object.optString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("brandName")) {
			if (!object.optString("brandName").equalsIgnoreCase("null")) {
				brandName = object.optString("brandName");
			}
		}
		if (object.has("brevityCode")) {
			if (!object.optString("brevityCode").equalsIgnoreCase("null")) {
				brevityCode = object.optString("brevityCode");
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getBrandName() {
		return brandName;
	}

	public String getBrevityCode() {
		return brevityCode;
	}

}
