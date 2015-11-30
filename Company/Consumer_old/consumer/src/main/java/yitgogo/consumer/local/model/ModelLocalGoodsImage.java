package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 2, "retailProductImgUrl":
 *             "http://images.yitos.net/images/public/20140906/5911410014969699.jpg"
 *             }
 */
public class ModelLocalGoodsImage {

	String id = "", retailProductImgUrl = "";

	public ModelLocalGoodsImage() {
	}

	public ModelLocalGoodsImage(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("retailProductImgUrl")) {
				if (!object.getString("retailProductImgUrl").equalsIgnoreCase(
						"null")) {
					retailProductImgUrl = object
							.optString("retailProductImgUrl");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getRetailProductImgUrl() {
		return retailProductImgUrl;
	}

	@Override
	public String toString() {
		return "ModelImage [id=" + id + ", retailProductImgUrl="
				+ retailProductImgUrl + "]";
	}

}