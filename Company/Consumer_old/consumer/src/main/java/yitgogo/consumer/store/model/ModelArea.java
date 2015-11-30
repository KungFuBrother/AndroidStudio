package yitgogo.consumer.store.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tiger
 * 
 * @JsonObject { "id": 2421, "valuename": "金牛区", "valuetype": { "id": 4,
 *             "typename": "区县" }, "onid": 269, "onname": null, "brevitycode":
 *             null }
 */
public class ModelArea {

	String id = "", valuename = "", onid = "", onname = "", brevitycode = "";
	ModelAreaType areaType = new ModelAreaType();

	public ModelArea() {
	}

	public ModelArea(JSONObject object) throws JSONException {
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("valuename")) {
			if (!object.getString("valuename").equalsIgnoreCase("null")) {
				valuename = object.optString("valuename");
			}
		}
		if (object.has("onid")) {
			if (!object.getString("onid").equalsIgnoreCase("null")) {
				onid = object.optString("onid");
			}
		}
		if (object.has("onname")) {
			if (!object.getString("onname").equalsIgnoreCase("null")) {
				onname = object.optString("onname");
			}
		}
		if (object.has("brevitycode")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				brevitycode = object.optString("brevitycode");
			}
		}
		if (object.has("valuetype")) {
			if (!object.getString("valuetype").equalsIgnoreCase("null")) {
				JSONObject typeObject = object.optJSONObject("valuetype");
				if (typeObject != null) {
					areaType = new ModelAreaType(typeObject);
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getValuename() {
		return valuename;
	}

	public String getOnid() {
		return onid;
	}

	public String getOnname() {
		return onname;
	}

	public String getBrevitycode() {
		return brevitycode;
	}

	public ModelAreaType getAreaType() {
		return areaType;
	}

	@Override
	public String toString() {
		return "ModelArea [id=" + id + ", valuename=" + valuename + ", onid="
				+ onid + ", onname=" + onname + ", brevitycode=" + brevitycode
				+ ", areaType=" + areaType + "]";
	}

}
