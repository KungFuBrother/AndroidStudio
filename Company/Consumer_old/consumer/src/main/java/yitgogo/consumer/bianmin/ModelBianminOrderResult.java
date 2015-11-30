package yitgogo.consumer.bianmin;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json "dataMap":{"sellPrice":"98.9", "orderNumber":"YT4261694182"}
 */
public class ModelBianminOrderResult {

	double sellPrice = -1;
	String orderNumber = "";

	public ModelBianminOrderResult(JSONObject object) {
		if (object != null) {
			if (object.has("sellPrice")) {
				if (!object.optString("sellPrice").equalsIgnoreCase("null")) {
					sellPrice = object.optDouble("sellPrice");
				}
			}
			if (object.has("orderNumber")) {
				if (!object.optString("orderNumber").equalsIgnoreCase("null")) {
					orderNumber = object.optString("orderNumber");
				}
			}
		}
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

}
