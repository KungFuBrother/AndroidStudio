package yitgogo.consumer.money.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json {"id":27,"name":"贵州"}
 * 
 * @Json {"id":751,"name" :"台北","province":"28"}
 */
public class ModelTakeOutArea {

	String id = "", name = "", province = "";

	public ModelTakeOutArea() {
	}

	public ModelTakeOutArea(JSONObject object) {
		if (object != null) {
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("name")) {
				if (!object.optString("name").equalsIgnoreCase("null")) {
					name = object.optString("name");
				}
			}
			if (object.has("province")) {
				if (!object.optString("province").equalsIgnoreCase("null")) {
					province = object.optString("province");
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

	public String getProvince() {
		return province;
	}

}
