package yitgogo.smart.model;

import org.json.JSONObject;

public class ModelMachineArea {

	String id = "", address = "", areas = "";
	String[] area={};

	public ModelMachineArea() {
	}

	public ModelMachineArea(JSONObject object) {
		if (object != null) {
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("address")) {
				if (!object.optString("address").equalsIgnoreCase("null")) {
					address = object.optString("address");
				}
			}
			if (object.has("areas")) {
				if (!object.optString("areas").equalsIgnoreCase("null")) {
					area=object.optString("areas").split(",");
					areas = object.optString("areas").replaceAll(",", ">");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public String getAreas() {
		return areas;
	}

	public String[] getArea() {
		return area;
	}
}
