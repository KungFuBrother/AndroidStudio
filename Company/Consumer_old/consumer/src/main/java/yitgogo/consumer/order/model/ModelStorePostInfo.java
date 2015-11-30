package yitgogo.consumer.order.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JSON { "whetherToUseStockSystem": false, "hawManyPackages": 50,
 *       "autoPurchase": false, "supportForDelivery": false, "postage": 10 }
 */
public class ModelStorePostInfo {

	double hawManyPackages = 0, postage = 0;
	boolean supportForDelivery = false;
	JSONObject jsonObject = new JSONObject();

	public ModelStorePostInfo() {
	}

	public ModelStorePostInfo(JSONObject object) {
		if (object != null) {
			this.jsonObject = object;
			if (object.has("hawManyPackages")) {
				if (!object.optString("hawManyPackages").equalsIgnoreCase(
						"null")) {
					hawManyPackages = object.optDouble("hawManyPackages");
				}
			}
			if (object.has("postage")) {
				if (!object.optString("postage").equalsIgnoreCase("null")) {
					postage = object.optDouble("postage");
				}
			}
			if (object.has("supportForDelivery")) {
				if (!object.optString("supportForDelivery").equalsIgnoreCase(
						"null")) {
					supportForDelivery = object
							.optBoolean("supportForDelivery");
				}
			}
		}
	}

	/**
	 * 满多少包邮
	 * 
	 * @return
	 */
	public double getHawManyPackages() {
		return hawManyPackages;
	}

	/**
	 * 邮费
	 * 
	 * @return
	 */
	public double getPostage() {
		return postage;
	}

	/**
	 * 是否支持货到付款
	 * 
	 * @return
	 */
	public boolean isSupportForDelivery() {
		return supportForDelivery;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
