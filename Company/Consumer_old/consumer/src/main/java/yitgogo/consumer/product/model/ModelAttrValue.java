package yitgogo.consumer.product.model;

import org.json.JSONObject;

public class ModelAttrValue {
	String id = "", name = "";
	String defaultId = "-1000";

	public ModelAttrValue(JSONObject object, int type) {
		switch (type) {

		case ModelAttrType.TYPE_BRAND:
			id = object.optString("id");
			name = object.optString("brandName");
			break;

		case ModelAttrType.TYPE_ATTR:
			id = object.optString("id");
			name = object.optString("attName");
			break;

		case ModelAttrType.TYPE_ATTR_EXTEND:
			id = object.optString("id");
			name = object.optString("attExtendName");
			break;

		default:
			break;
		}
	}

	public ModelAttrValue() {
		id = defaultId;
		name = "不限";
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ModelAttrValue [id=" + id + ", name=" + name + ", defaultId="
				+ defaultId + "]";
	}

}
