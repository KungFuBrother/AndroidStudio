package yitgogo.consumer.bianmin.traffic.model;

import org.json.JSONObject;

public class ModelCarType {

	String id = "", name = "";

	public ModelCarType() {
	}

	public ModelCarType(JSONObject object) {
		if (object != null) {
			if (object.has("value1")) {
				if (!object.optString("value1").equalsIgnoreCase("null")) {
					id = object.optString("value1");
				}
			}
			if (object.has("value2")) {
				if (!object.optString("value2").equalsIgnoreCase("null")) {
					name = object.optString("value2");
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

}
