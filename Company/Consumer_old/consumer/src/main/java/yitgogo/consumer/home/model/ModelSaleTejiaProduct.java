package yitgogo.consumer.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * { "img":
 * "http://images.yitos.net/images/public/20150708/98591436346988459.png",
 * "productId": 1339 }
 * 
 * @author Tiger
 * 
 */
public class ModelSaleTejiaProduct {

	String img = "", productId = "";

	public ModelSaleTejiaProduct() {
	}

	public ModelSaleTejiaProduct(JSONObject object) throws JSONException {
		if (object != null) {
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
			if (object.has("productId")) {
				if (!object.getString("productId").equalsIgnoreCase("null")) {
					productId = object.optString("productId");
				}
			}
		}
	}

	public String getImg() {
		return img;
	}

	public String getProductId() {
		return productId;
	}

	@Override
	public String toString() {
		return "ModelSaleTejiaProduct [img=" + img + ", productId=" + productId
				+ "]";
	}

}
