package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject "classValueBean": { "id": 4, "classValueName": "休闲", "classImg":
 *             null, "organizationId": "1068" }
 * 
 */
public class ModelLocalServiceClass {

	String id = "", classValueName = "", classImg = "", organizationId = "";

	public ModelLocalServiceClass() {
		classValueName = "所有";
	}

	public ModelLocalServiceClass(JSONObject object) throws JSONException {

		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("classValueName")) {
				if (!object.getString("classValueName")
						.equalsIgnoreCase("null")) {
					classValueName = object.optString("classValueName");
				}
			}
			if (object.has("classImg")) {
				if (!object.getString("classImg").equalsIgnoreCase("null")) {
					classImg = object.optString("classImg");
				}
			}
			if (object.has("organizationId")) {
				if (!object.getString("organizationId")
						.equalsIgnoreCase("null")) {
					organizationId = object.optString("organizationId");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getClassValueName() {
		return classValueName;
	}

	public String getClassImg() {
		return classImg;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	@Override
	public String toString() {
		return "ModelServiceClass [id=" + id + ", classValueName="
				+ classValueName + ", organizationId=" + organizationId + "]";
	}

}
