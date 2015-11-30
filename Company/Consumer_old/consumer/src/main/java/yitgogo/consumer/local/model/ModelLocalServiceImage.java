package yitgogo.consumer.local.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject "imgs": [ { "id": 7, "imgName":
 *             "http://images.yitos.net/images/public/20140907/29391410098513531.png"
 *             } ]
 * 
 */
public class ModelLocalServiceImage {

	String id = "", imgName = "";

	public ModelLocalServiceImage() {
	}

	public ModelLocalServiceImage(JSONObject object) {
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
		return "ModelLocalServiceImage [id=" + id + ", imgName=" + imgName
				+ "]";
	}

}