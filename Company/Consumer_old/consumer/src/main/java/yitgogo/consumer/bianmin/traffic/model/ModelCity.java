package yitgogo.consumer.bianmin.traffic.model;

import org.json.JSONObject;

public class ModelCity {

	String id = "", name = "", provinceId = "";

	public ModelCity() {
	}

	public ModelCity(JSONObject object) {
		if (object != null) {
			if (object.has("value1")) {
				if (!object.optString("value1").equalsIgnoreCase("null")) {
					provinceId = object.optString("value1");
				}
			}
			if (object.has("value2")) {
				if (!object.optString("value2").equalsIgnoreCase("null")) {
					id = object.optString("value2");
				}
			}
			if (object.has("value3")) {
				if (!object.optString("value3").equalsIgnoreCase("null")) {
					name = object.optString("value3");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProvinceId() {
		return provinceId;
	}

}
