package com.dtr.zxing.model;

import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "orderType": "1", "orderNumbers": "YT56481123223,YT56481123223",
 *       "totalMoney": "10.00" }
 */
public class ModelQRCodeOrder {

	int orderType = 0;
	String orderNumbers = "";
	double totalMoney = 0;

	public ModelQRCodeOrder(JSONObject object) {
		if (object != null) {
			if (object.has("orderNumbers")) {
				if (!object.optString("orderNumbers").equalsIgnoreCase("null")) {
					orderNumbers = object.optString("orderNumbers");
				}
			}
			if (object.has("totalMoney")) {
				if (!object.optString("totalMoney").equalsIgnoreCase("null")) {
					totalMoney = object.optDouble("totalMoney");
				}
			}
			orderType = object.optInt("orderType");
		}
	}

	public int getOrderType() {
		return orderType;
	}

	public String getOrderNumbers() {
		return orderNumbers;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

}
