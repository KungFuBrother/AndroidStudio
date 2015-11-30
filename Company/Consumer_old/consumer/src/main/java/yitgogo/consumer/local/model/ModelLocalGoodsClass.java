package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "id": 1, "retailProdTypeValueName": "农副产品",
 *             "retailClassTypeBean": { "id": 1, "retailProductType": "大类" }
 */
public class ModelLocalGoodsClass {

	String id = "", retailProdTypeValueName = "", img = "";

	public ModelLocalGoodsClass() {
		retailProdTypeValueName = "所有";
	}

	public ModelLocalGoodsClass(JSONObject object) throws JSONException {

		if (object != null) {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("retailProdTypeValueName")) {
				if (!object.getString("retailProdTypeValueName")
						.equalsIgnoreCase("null")) {
					retailProdTypeValueName = object
							.optString("retailProdTypeValueName");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getRetailProdTypeValueName() {
		return retailProdTypeValueName;
	}

	public String getImg() {
		return img;
	}

	@Override
	public String toString() {
		return "ModelLocalGoodsClass [id=" + id + ", retailProdTypeValueName="
				+ retailProdTypeValueName + ", img=" + img + "]";
	}

}
