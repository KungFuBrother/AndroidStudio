package yitgogo.consumer.product.model;

import org.json.JSONObject;

public class ModelProductImage {

	String id = "", imgName = "";

	public ModelProductImage(JSONObject object) {
		if (object != null) {
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("imgName")) {
				if (!object.optString("imgName").equalsIgnoreCase("null")) {
					imgName = object.optString("imgName");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getImgName() {
		return imgName;
	}

	@Override
	public String toString() {
		return "ProductImage [id=" + id + ", imgName=" + imgName + "]";
	}

}
